package com.aysusen.financetracker.model

data class CurrencyResponse(
    val id: Int,
    val code: String, // "USD", "EUR"
    val name: String, // "US Dollar"
    val rateToTRY: Double
)