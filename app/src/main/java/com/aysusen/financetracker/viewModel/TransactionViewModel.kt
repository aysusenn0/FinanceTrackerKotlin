package com.aysusen.financetracker.viewModel

import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aysusen.financetracker.model.TransactionRequest
import com.aysusen.financetracker.model.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {

    sealed class TransactionState {
        object Idle : TransactionState()
        object Loading : TransactionState()
        data class Success(val message: String = "İşlem başarılı") : TransactionState()
        data class Error(val message: String) : TransactionState()
    }

    sealed class TransactionListState {
        object Idle : TransactionListState()
        object Loading : TransactionListState()
        data class Success(val transactions: List<TransactionResponse>) : TransactionListState()
        data class Error(val message: String) : TransactionListState()
    }

    // State for creating/updating transactions
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState.asStateFlow()

    // State for fetching transaction list
    private val _transactionListState = MutableStateFlow<TransactionListState>(TransactionListState.Idle)
    val transactionListState: StateFlow<TransactionListState> = _transactionListState.asStateFlow()

    // Keep for backward compatibility
    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions: StateFlow<List<TransactionResponse>> = _transactions.asStateFlow()

    fun createTransaction(
        title: String,
        amount: Double,
        currencyId: Int,
        transactionTypeId: Int
    ) {
        _transactionState.value = TransactionState.Loading
        Log.d("TransactionViewModel", "Creating transaction: $title, $amount")

        viewModelScope.launch {
            try {
                val requestBody = TransactionRequest(
                    title = title,
                    amount = amount,
                    currencyId = currencyId,
                    transactionTypeId = transactionTypeId
                )

                val response = RetrofitClient.instance.createTransaction(requestBody)

                if (response.isSuccessful) {
                    _transactionState.value = TransactionState.Success(
                        "İşlem başarıyla kaydedildi"
                    )
                    Log.d("TransactionViewModel", "Transaction created successfully")

                    // Optionally refresh transaction list
                    fetchTransactions()
                } else {
                    val errorMsg = "Sunucu hatası: ${response.code()}"
                    _transactionState.value = TransactionState.Error(errorMsg)
                    Log.e("TransactionViewModel", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Bağlantı hatası: ${e.message}"
                _transactionState.value = TransactionState.Error(errorMsg)
                Log.e("TransactionViewModel", errorMsg, e)
            }
        }
    }

    fun fetchTransactions() {
        _transactionListState.value = TransactionListState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getTransaction()

                if (response.isSuccessful) {
                    val transactionList = response.body() ?: emptyList()
                    _transactions.value = transactionList
                    _transactionListState.value = TransactionListState.Success(transactionList)
                    Log.d("TransactionViewModel", "Transactions fetched: ${transactionList.size}")
                } else {
                    val errorMsg = "Sunucu hatası: ${response.code()}"
                    _transactionListState.value = TransactionListState.Error(errorMsg)
                    Log.e("TransactionViewModel", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Bağlantı hatası: ${e.message}"
                _transactionListState.value = TransactionListState.Error(errorMsg)
                Log.e("TransactionViewModel", errorMsg, e)
            }
        }
    }

    fun resetTransactionState() {
        _transactionState.value = TransactionState.Idle
    }

    fun resetTransactionListState() {
        _transactionListState.value = TransactionListState.Idle
    }
}