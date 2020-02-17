package com.ifmo.hexxlet.service

import com.ifmo.hexxlet.exception.MoneyTransferException
import com.ifmo.hexxlet.model.Account
import com.ifmo.hexxlet.model.OperationType
import com.ifmo.hexxlet.model.OperationVo
import com.ifmo.hexxlet.model.TransferVo
import com.ifmo.hexxlet.repository.AccountRepository
import com.ifmo.hexxlet.repository.OperationRepository
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSession
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.math.BigDecimal

class OperationService(private val session: SqlSession) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val operationRepository = session.getMapper(OperationRepository::class.java)
    private val accountRepository = session.getMapper(AccountRepository::class.java)

    fun getOperationsOfAccount(accountId: Int): List<OperationVo>{
        val operationsOfAccount = operationRepository.getOperationsOfAccount(accountId)
        return operationsOfAccount.map { val operationVo = OperationVo(it)
            operationVo.operationType = if (accountId.toString() == it.to) OperationType.INCOME else OperationType.OUTCOME
            operationVo
        }
    }

    fun transferMoney(transferVo: TransferVo): Boolean {
        logger.info("Starting transfer ${transferVo.amount} from ${transferVo.from} to ${transferVo.to}")

        val fromAccount = accountRepository.getAccount(transferVo.from.toInt())
        val toAccount = accountRepository.getAccount(transferVo.to.toInt())

        checkIfTransferPossible(fromAccount, toAccount, transferVo)
        val fromBalance = calculateFromBalance(fromAccount!!, transferVo)

        return try {
            var rowsUpdated = accountRepository.updateBalance(fromAccount.id, fromBalance, fromAccount.version)
            if (rowsUpdated == 0)
                throw MoneyTransferException("Another transaction going concurrently")
            rowsUpdated = accountRepository.updateBalance(toAccount!!.id, toAccount.balance + transferVo.amount.toBigDecimal(), toAccount.version)
            if (rowsUpdated == 0)
                throw MoneyTransferException("Another transaction going concurrently")
            operationRepository.createOperation(transferVo)
            session.commit()
            true
        } catch (e: Exception){
            logger.error(ExceptionUtils.getStackTrace(e))
            session.rollback()
            logger.warn("Rolling back transaction")
            false
        }
    }

    private fun checkIfTransferPossible(fromAccount: Account?, toAccount: Account?, transferVo: TransferVo){
        if (fromAccount == null || toAccount == null) {
            val message = "account with id=${if (fromAccount == null) transferVo.from else transferVo.to} does not exist"
            logger.warn(message)
            throw MoneyTransferException(message)
        }
        if (fromAccount == toAccount){
            logger.warn("attempt to transfer money to source account (id = ${fromAccount.id})")
            throw MoneyTransferException("source and destination accounts can not be equal")
        }
        if (transferVo.amount.toBigDecimal() <= BigDecimal.ZERO){
            logger.warn("attempt to transfer negative amount of money")
            throw MoneyTransferException("transfer amount must be greater than 0")
        }
    }

    private fun calculateFromBalance(fromAccount: Account, transferVo: TransferVo): BigDecimal {
        val fromBalance = fromAccount.balance - transferVo.amount.toBigDecimal()
        if (fromBalance < BigDecimal.ZERO) {
            logger.warn("Transfer aborted - insufficient funds on account ${fromAccount.id}")
            throw MoneyTransferException("Insufficient funds on account ${fromAccount.id}")
        }
        return fromBalance
    }
}