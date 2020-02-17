package com.ifmo.hexxlet.model

import java.time.format.DateTimeFormatter

class AccountVo(val id: String, val balance: String){
    constructor(account: Account) : this(account.id.toString(), account.balance.setScale(2).toString())
}

class TransferVo(val from: String, val to: String, val amount: String)

class OperationVo(operation: Operation){
    lateinit var operationType: OperationType
    val id: String = operation.operationId
    val from: String = operation.from
    val to: String = operation.to
    val amount: String = operation.amount.setScale(2).toString()
    val time: String = operation.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

open class BaseResponse(val status: Status, val message: String?){
    constructor(status: Status) : this(status, null)
}

class AccountResp(val accountId: String, status: Status) : BaseResponse(status)

enum class Status{
    OK, BAD
}

enum class OperationType{
    INCOME, OUTCOME
}
