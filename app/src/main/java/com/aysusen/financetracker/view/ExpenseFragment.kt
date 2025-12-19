package com.aysusen.financetracker.view

import com.aysusen.financetracker.BaseTransactionFragment

class ExpenseFragment : BaseTransactionFragment() {

    override fun getTransactionTypeId(): Int = 2 // Gider type ID

    override fun getLogTag(): String = "ExpenseFragment"
}