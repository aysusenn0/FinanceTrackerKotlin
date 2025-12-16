package com.aysusen.financetracker.adapter

import androidx.recyclerview.widget.DiffUtil
import com.aysusen.financetracker.model.TransactionResponse

// DiffUtil.ItemCallback, listenin verimli güncellenmesi için gereklidir.
class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionResponse>() {
    override fun areItemsTheSame(
        oldItem: TransactionResponse, newItem: TransactionResponse
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TransactionResponse, newItem: TransactionResponse
    ): Boolean {
        return oldItem == newItem
    }
}
