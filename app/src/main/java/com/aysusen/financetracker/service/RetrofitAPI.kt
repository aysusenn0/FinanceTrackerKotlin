package com.aysusen.financetracker.service

import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.model.TransactionRequest
import com.aysusen.financetracker.model.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitAPI {

    @GET ("api/currencies")
    suspend fun getCurrencies () :Response<List<CurrencyResponse>>

    @POST("api/transactions")
    suspend fun createTransaction (@Body transaction : TransactionRequest):Response<Unit>

    @GET("api/transactions")
    suspend fun getTransaction ():Response<List<TransactionResponse>>

}