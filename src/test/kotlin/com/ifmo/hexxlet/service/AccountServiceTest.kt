package com.ifmo.hexxlet.service

import com.ifmo.hexxlet.configureMyBatis
import com.ifmo.hexxlet.exception.MoneyTransferException
import com.ifmo.hexxlet.getProperties
import com.ifmo.hexxlet.initDatabase
import com.ifmo.hexxlet.model.AccountVo
import org.apache.ibatis.session.SqlSessionFactory
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.sql.Connection
import kotlin.test.assertNotEquals

@RunWith(JUnit4::class)
class AccountServiceTest {

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

    @Test(expected = MoneyTransferException::class)
    fun createAccountNegativeBalance() {
        sessionFactory.openSession().use {
            val accountService = AccountService(sessionFactory.openSession())
            val accountVo = AccountVo("", "-500")
            accountService.createAccount(accountVo)
        }
    }

    @Test
    fun accountIdReturned(){
        sessionFactory.openSession().use {
            val accountVo = AccountVo("", "100")
            val accountService = AccountService(sessionFactory.openSession())
            accountService.createAccount(accountVo)
            //already accounts created, our is not 1st
            assertNotEquals("1", accountVo.id)
        }
    }
}