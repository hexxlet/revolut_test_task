package com.ifmo.hexxlet.service

import com.ifmo.hexxlet.model.AccountVo
import com.ifmo.hexxlet.exception.MoneyTransferException
import com.ifmo.hexxlet.repository.AccountRepository
import org.apache.ibatis.session.SqlSession
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class AccountService(session: SqlSession) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val accountRepository = session.getMapper(AccountRepository::class.java)

    fun createAccount(accountVo: AccountVo): Int{
        val accBalance = accountVo.balance.toBigDecimal()
        if (accBalance < BigDecimal.ZERO) {
            logger.warn("attempt to create account with negative balance")
            throw MoneyTransferException("Account balance can not be less than 0")
        }
        return accountRepository.createAccount(accountVo)
    }
}