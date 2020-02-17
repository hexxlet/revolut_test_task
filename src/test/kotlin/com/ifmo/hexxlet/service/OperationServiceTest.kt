package com.ifmo.hexxlet.service

import com.ifmo.hexxlet.configureMyBatis
import com.ifmo.hexxlet.exception.MoneyTransferException
import com.ifmo.hexxlet.getProperties
import com.ifmo.hexxlet.initDatabase
import com.ifmo.hexxlet.model.AccountVo
import com.ifmo.hexxlet.model.TransferVo
import com.ifmo.hexxlet.repository.AccountRepository
import org.apache.ibatis.session.SqlSessionFactory
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.sql.Connection

@RunWith(JUnit4::class)
class OperationServiceTest {

    companion object {
        lateinit var sessionFactory: SqlSessionFactory
        lateinit var connection: Connection

        @BeforeClass
        @JvmStatic fun setup() {
            val settings = getProperties("/h2.properties")
            connection = initDatabase(settings)
            sessionFactory = configureMyBatis(settings)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            connection.close()
        }
    }

    @Test
    fun getOperationsOfAccount() {
        sessionFactory.openSession().use {
            val operationService = OperationService(it)
            operationService.transferMoney(TransferVo("1", "2", "100"))
            val operationsOfFirstAccount = operationService.getOperationsOfAccount(1)
            val operationsOfSecondAccount = operationService.getOperationsOfAccount(2)
            assertEquals(1, operationsOfFirstAccount.size)
            assertEquals(1, operationsOfSecondAccount.size)
            assertEquals(operationsOfFirstAccount.first().amount, operationsOfSecondAccount.first().amount)
            assertEquals(operationsOfFirstAccount.first().to, operationsOfSecondAccount.first().to)
            assertEquals(operationsOfFirstAccount.first().from, operationsOfSecondAccount.first().from)
        }
    }

    @Test(expected = MoneyTransferException::class)
    fun transferNegativeMoney() {
        sessionFactory.openSession().use {
            val operationService = OperationService(it)
            operationService.transferMoney(TransferVo("1", "2", "-100"))
        }
    }

    @Test(expected = MoneyTransferException::class)
    fun transferSameAccount() {
        sessionFactory.openSession().use {
            val operationService = OperationService(it)
            val accountService = AccountService(it)
            val accountVo = AccountVo("", "100")
            accountService.createAccount(accountVo)
            operationService.transferMoney(TransferVo(accountVo.id, accountVo.id, "100"))
        }
    }

    @Test(expected = MoneyTransferException::class)
    fun transferNotExistingAccount() {
        sessionFactory.openSession().use {
            val operationService = OperationService(it)
            operationService.transferMoney(TransferVo("99", "100", "100"))
        }
    }

    @Test
    fun transferMoney(){
        sessionFactory.openSession().use {
            val operationService = OperationService(it)
            val accountService = AccountService(it)
            val accountVoOne = AccountVo("", "100")
            val accountVoTwo = AccountVo("", "500.15")
            accountService.createAccount(accountVoOne)
            accountService.createAccount(accountVoTwo)

            assertTrue(operationService.transferMoney(TransferVo(accountVoOne.id, accountVoTwo.id, "100")))
            val accountRepository = it.getMapper(AccountRepository::class.java)
            val first = accountRepository.getAccount(accountVoOne.id.toInt())
            val second = accountRepository.getAccount(accountVoTwo.id.toInt())
            assertEquals("0.0000".toBigDecimal(), first?.balance)
            assertEquals("600.1500".toBigDecimal(), second?.balance)
        }
    }
}