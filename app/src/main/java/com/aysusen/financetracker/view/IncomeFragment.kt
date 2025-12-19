package com.aysusen.financetracker.view

import com.aysusen.financetracker.BaseTransactionFragment

class IncomeFragment : BaseTransactionFragment() {

    override fun getTransactionTypeId(): Int = 1 // gelir type ID

    override fun getLogTag(): String = "IncomeFragment"
}