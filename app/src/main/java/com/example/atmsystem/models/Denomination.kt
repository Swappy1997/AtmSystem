package com.example.atmsystem.models

data class Denomination(val value: Int, var count: Int)
data class Transaction( val denominations: List<Denomination>)



