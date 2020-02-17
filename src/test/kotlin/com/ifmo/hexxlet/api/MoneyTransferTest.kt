package com.ifmo.hexxlet.api

import com.ifmo.hexxlet.*
import com.ifmo.hexxlet.model.AccountVo
import com.ifmo.hexxlet.model.TransferVo
import io.restassured.RestAssured
import io.restassured.RestAssured.`when`
import org.apache.ibatis.session.SqlSessionFactory
import org.hamcrest.CoreMatchers.equalTo
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection


class MoneyTransferTest {

    companion object {
        lateinit var sessionFactory: SqlSessionFactory
        lateinit var connection: Connection

        @BeforeClass
        @JvmStatic fun setup() {
            val settings = getProperties("/h2.properties")
            connection = initDatabase(settings)
            sessionFactory = configureMyBatis(settings)
            configureEmbeddedServer()
            enableEndpoints(sessionFactory)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            connection.close()
        }
    }

    @Test
    fun testNewAccount(){
        val request = RestAssured.given()
        request.header("Content-Type", "application/json")
        request.body(AccountVo("", "147.73"))
        val response = request.post("/accounts/new")
        val accountId = response.jsonPath().get<String>("accountId")
        `when`().get("/accounts/info/$accountId").then().body("balance", equalTo("147.73"))
    }

    @Test
    fun testTransfer(){
        val requestFirstAcc = RestAssured.given()
        requestFirstAcc.header("Content-Type", "application/json")
        requestFirstAcc.body(AccountVo("", "22"))
        val responseFirstAcc = requestFirstAcc.post("/accounts/new")
        val accountIdFirstAcc = responseFirstAcc.jsonPath().get<String>("accountId")

        val requestSecondAcc = RestAssured.given()
        requestSecondAcc.header("Content-Type", "application/json")
        requestSecondAcc.body(AccountVo("", "34"))
        val responseSecondAcc = requestSecondAcc.post("/accounts/new")
        val accountIdSecondAcc = responseSecondAcc.jsonPath().get<String>("accountId")

        val transferReq = RestAssured.given()
        transferReq.header("Content-Type", "application/json")
        transferReq.body(TransferVo(accountIdFirstAcc, accountIdSecondAcc, "22"))
        val transferResp = transferReq.post("/transfers")

        val statusCode: Int = transferResp.getStatusCode()
        Assert.assertEquals(201, statusCode)

        `when`().get("/accounts/info/$accountIdFirstAcc").then().body("balance", equalTo("0.00"))
        `when`().get("/accounts/info/$accountIdSecondAcc").then().body("balance", equalTo("56.00"))
    }
}
