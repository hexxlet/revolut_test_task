package com.ifmo.hexxlet

import com.google.gson.Gson
import com.ifmo.hexxlet.exception.MoneyTransferException
import com.ifmo.hexxlet.model.*
import com.ifmo.hexxlet.model.Status.BAD
import com.ifmo.hexxlet.model.Status.OK
import com.ifmo.hexxlet.repository.AccountRepository
import com.ifmo.hexxlet.repository.OperationRepository
import com.ifmo.hexxlet.service.AccountService
import com.ifmo.hexxlet.service.OperationService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.TransactionIsolationLevel
import org.slf4j.LoggerFactory
import spark.Spark.get
import spark.Spark.post


fun main(args: Array<String>) {
    val settings = getProperties("/h2.properties")
    initDatabase(settings)
    val sessionFactory = configureMyBatis(settings)
    configureEmbeddedServer()
    enableEndpoints(sessionFactory)
}

fun enableEndpoints(sessionFactory: SqlSessionFactory) {
    val gson = Gson()
    val logger = LoggerFactory.getLogger("ENDPOINTS_LOGGER")

    post("/accounts/new"){ req, resp ->
        logger.info("/accounts/new requested with payload:\n${req.body()}")
        resp.type("application/json")
        sessionFactory.openSession(true).use { session ->
            try {
                val accountVo = gson.fromJson(req.body(), AccountVo::class.java)
                val accountService = AccountService(session)
                accountService.createAccount(accountVo)
                logger.info("account with ${accountVo.id} created")
                resp.status(201)
                gson.toJson(AccountResp(accountVo.id, OK))
            }
            catch (e: MoneyTransferException){
                resp.status(418)
                gson.toJson(BaseResponse(BAD, e.message))
            }
            catch (e: RuntimeException){
                logger.error(ExceptionUtils.getStackTrace(e))
                resp.status(500)
                gson.toJson(BaseResponse(BAD))
            }
        }
    }

    get("/accounts/info/:id"){ req, resp ->
        logger.info("/accounts/info for account: ${req.params("id")}")
        resp.type("application/json")
        sessionFactory.openSession().use { session ->
            try {
                val accountId = req.params("id").toInt()
                val accountRepository = session.getMapper(AccountRepository::class.java)
                val account = accountRepository.getAccount(accountId)
                    ?: return@get gson.toJson(BaseResponse(BAD, "No account found with id=$accountId"))
                logger.info("data retrieved from account with id=$accountId")
                gson.toJson(AccountVo(account))
            }
            catch (e: RuntimeException){
                logger.error(ExceptionUtils.getStackTrace(e))
                resp.status(500)
                gson.toJson(BaseResponse(BAD))
            }
        }
    }

    post("/transfers") {req, resp ->
        logger.info("/transfers requested with payload:\n${req.body()}")
        resp.type("application/json")
        sessionFactory.openSession(false).use { session ->
            try {
                val transferDto = gson.fromJson(req.body(), TransferVo::class.java)
                val operationService = OperationService(session)
                val successed = operationService.transferMoney(transferDto)
                if (successed) {
                    resp.status(201)
                    gson.toJson(BaseResponse(OK))
                }
                else gson.toJson(BaseResponse(BAD, "Transfer can not be conducted"))
            }
            catch (e: MoneyTransferException){
                resp.status(418)
                gson.toJson(BaseResponse(BAD, e.message))
            }
            catch (e: RuntimeException){
                logger.error(ExceptionUtils.getStackTrace(e))
                resp.status(500)
                gson.toJson(BaseResponse(BAD))
            }
        }
    }

    get("/accounts/history/:id") { req, resp ->
        logger.info("/account/history for account: ${req.params("id")}")
        resp.type("application/json")
        sessionFactory.openSession().use { session ->
            try {
                val accountId = req.params("id").toInt()
                val operationService = OperationService(session)
                val operations = operationService.getOperationsOfAccount(accountId)
                gson.toJson(operations)
            }
            catch (e: RuntimeException){
                logger.error(ExceptionUtils.getStackTrace(e))
                resp.status(500)
                gson.toJson(BaseResponse(BAD))
            }
        }
    }
}

