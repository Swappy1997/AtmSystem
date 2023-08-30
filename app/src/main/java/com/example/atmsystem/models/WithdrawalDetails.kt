package com.example.atmsystem.models

data class WithdrawalDetails(
    val invalidMessage:String?=null,
    val denominations: List<Denomination>?=null,
    val backgroundColor: Int? = null,
    val timestamp: String?=null,
    val transaction: List<Transaction>?=null
)


