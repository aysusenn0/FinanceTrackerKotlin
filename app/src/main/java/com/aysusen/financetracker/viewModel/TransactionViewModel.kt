package com.aysusen.financetracker.viewModel

import RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.model.TransactionRequest
import com.aysusen.financetracker.model.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    sealed class TransactionState() {
        object Idle : TransactionState() // Boşta
        object Loading : TransactionState() // Yükleniyor
        object Success : TransactionState() // Başarılı
        data class Error(val message: String) : TransactionState() // Hatalı
    }

    private val _currencies = MutableStateFlow<List<CurrencyResponse>>(emptyList())
    val currencies: StateFlow<List<CurrencyResponse>> = _currencies

    // Hata durumlarını yönetmek için bir akış
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState
    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions: StateFlow<List<TransactionResponse>> = _transactions

    fun createTransaction(title: String, amount: Double, currencyId: Int, transactionTypeId: Int) {
        // Durumu "Yükleniyor" olarak ayarla
        _transactionState.value = TransactionState.Loading

        viewModelScope.launch {
            try {
                val requestBody = TransactionRequest(
                    title = title,
                    amount = amount,
                    currencyId = currencyId,
                    transactionTypeId = transactionTypeId
                )

                // Retrofit ile API'yi (POST /api/transactions) çağır
                val response = RetrofitClient.instance.createTransaction(requestBody)

                if (response.isSuccessful) {
                    // Başarılıysa durumu güncelle
                    _transactionState.value = TransactionState.Success
                } else {
                    // Sunucudan hata geldiyse (400, 500 vb.)
                    _transactionState.value =
                        TransactionState.Error("Sunucu hatası: ${response.code()}")
                }
            } catch (e: Exception) {
                // İnternet yoksa veya başka bir bağlantı hatası
                _transactionState.value = TransactionState.Error("Bağlantı hatası: ${e.message}")
            }
        }
    }

    fun fetchCurrencies() {
        // viewModelScope, bu Coroutine'in ViewModel yaşadığı sürece çalışmasını sağlar
        viewModelScope.launch {
            try {
                // 1. Ağ isteğini atıyoruz (RetrofitClient'ı kullanarak)
                val response = RetrofitClient.instance.getCurrencies()

                // 2. Cevabı kontrol ediyoruz
                if (response.isSuccessful) {
                    // Başarılıysa, gelen veriyi StateFlow'a aktarıyoruz
                    _currencies.value = response.body() ?: emptyList()
                    _error.value = null // Eski hatayı temizle
                } else {
                    // Sunucudan hata geldiyse (örn: 404, 500)
                    _error.value = "Sunucu hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                // 3. Hata yakalama (örn: İnternet yoksa veya HTTPS/Sertifika hatası)
                // O DİKKAT dediğim localhost ve HTTPS hatası buraya düşecek.
                _error.value = "Bağlantı hatası: ${e.message}"
            }
        }
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getTransaction()
                if (response.isSuccessful) {
                    _transactionState.value = TransactionState.Success
                    _transactions.value = response.body() ?: emptyList()
                } else {
                    _transactionState.value =
                        TransactionState.Error("Sunucu hatası: ${response.code()}")
                }

            } catch (e: Exception) {
                _transactionState.value = TransactionState.Error("Bağlantı hatası: ${e.message}")

            }

        }
    }

}