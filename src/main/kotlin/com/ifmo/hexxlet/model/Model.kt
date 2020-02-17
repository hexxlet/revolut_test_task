package com.ifmo.hexxlet.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Account(val id: Int, val balance: BigDecimal, val version: Int)

data class Operation(val operationId: String, val from: String, val to: String, val amount: BigDecimal, val time: LocalDateTime)
