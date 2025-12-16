package com.aysusen.financetracker.model

data class TransactionRequest(
    val title: String,
    val amount: Double,
    val currencyId: Int,
    val transactionTypeId: Int
)
// 1: Gelir, 2: Gider (Backend'e bağlı)