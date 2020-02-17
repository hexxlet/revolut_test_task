package com.ifmo.hexxlet.repository

import com.ifmo.hexxlet.model.Operation
import com.ifmo.hexxlet.model.TransferVo
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface OperationRepository {

    @Select("SELECT * FROM OPERATION O WHERE O.FROM_ID = #{accountId} OR O.TO_ID = #{accountId}")
    fun getOperationsOfAccount(accountId: Int): List<Operation>

    @Insert("INSERT INTO OPERATION (FROM_ID, TO_ID, AMOUNT, TIME) VALUES(#{from}, #{to}, #{amount},  CURRENT_TIMESTAMP())")
    fun createOperation(operation: TransferVo): Int
}