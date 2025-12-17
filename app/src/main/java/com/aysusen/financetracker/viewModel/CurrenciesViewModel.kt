package com.aysusen.financetracker.viewModel

import RetrofitClient
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aysusen.financetracker.model.CurrencyResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrenciesViewModel : ViewModel() {

    sealed class CurrencyState {
        object Idle : CurrencyState()
        object Loading : CurrencyState()
        data class Success(val currencies: List<CurrencyResponse>) : CurrencyState()
        data class Error(val message: String) : CurrencyState()
    }

    private val _currencyState = MutableStateFlow<CurrencyState>(CurrencyState.Idle)
    val currencyState: StateFlow<CurrencyState> = _currencyState.asStateFlow()

    // Keep this for backward compatibility with existing code
    private val _currencies = MutableStateFlow<List<CurrencyResponse>>(emptyList())
    val currencies: StateFlow<List<CurrencyResponse>> = _currencies.asStateFlow()

    fun fetchCurrencies() {
        Log.d("CurrenciesViewModel", "fetchCurrencies called")

        viewModelScope.launch {
            _currencyState.value = CurrencyState.Loading

            try {
                val response = RetrofitClient.instance.getCurrencies()

                if (response.isSuccessful) {
                    val currencyList = response.body() ?: emptyList()
                    _currencies.value = currencyList
                    _currencyState.value = CurrencyState.Success(currencyList)
                    Log.d("CurrenciesViewModel", "Currencies fetched: ${currencyList.size}")
                } else {
                    val errorMsg = "Sunucu hatası: ${response.code()}"
                    _currencyState.value = CurrencyState.Error(errorMsg)
                    Log.e("CurrenciesViewModel", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Bağlantı hatası: ${e.message}"
                _currencyState.value = CurrencyState.Error(errorMsg)
                Log.e("CurrenciesViewModel", errorMsg, e)
            }
        }
    }

    fun resetState() {
        _currencyState.value = CurrencyState.Idle
    }
}