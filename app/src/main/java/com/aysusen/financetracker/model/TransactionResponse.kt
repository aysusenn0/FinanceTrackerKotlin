package com.aysusen.financetracker.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("id")  //çektiğimiz nameler burada
    val id: Int, //aynı olmasına gerek yoktu

    @SerializedName("title")
    val title: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("date")
    val date: String,

    @SerializedName("currencyCode")
    val currencyCode: String,

    @SerializedName("typeName")
    val typeName: String,

    @SerializedName("transactionTypeId")
    val transactionTypeId: Int
)
