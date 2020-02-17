package com.ifmo.hexxlet.repository

import com.ifmo.hexxlet.model.Account
import com.ifmo.hexxlet.model.AccountVo
import org.apache.ibatis.annotations.*
import java.math.BigDecimal

@Mapper
interface AccountRepository {

    @Select("SELECT * FROM ACCOUNT")
    fun getAccounts(): List<Account>

    @Options(useGeneratedKeys = true, keyProperty="id")
    @Insert("INSERT INTO ACCOUNT (BALANCE) VALUES (#{balance})")
    fun createAccount(account: AccountVo): Int

    @Update("UPDATE ACCOUNT A SET A.BALANCE=#{balance}, A.VERSION=A.VERSION + 1 WHERE A.ID=#{accountId} AND A.VERSION = #{version}")
    fun updateBalance(@Param("accountId") accountId: Int, @Param("balance") balance: BigDecimal, @Param("version") version: Int): Int

    @Select("SELECT * FROM ACCOUNT A WHERE A.ID = #{accountId}")
    fun getAccount(accountId: Int): Account?
}