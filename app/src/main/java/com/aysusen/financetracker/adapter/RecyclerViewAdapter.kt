package com.aysusen.financetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aysusen.financetracker.model.TransactionResponse
import com.aysusen.financetracker.databinding.ItemTransactionBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale


class RecyclerViewAdapter :
    ListAdapter<TransactionResponse, RecyclerViewAdapter.TransactionViewHolder>(
        TransactionDiffCallback()
    ) {

    // formatlama
    private val currencyFormatter = DecimalFormat("#,##0.00")

    // Tarih formatlayıcı
    private val inputDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val outputDateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionResponse) = with(binding) {
            textViewTitle.text = transaction.title

            // Tarihi formatla
            try {
                val date = inputDateFormatter.parse(transaction.date)
                textViewDate.text = date?.let { outputDateFormatter.format(it) } ?: transaction.date
            } catch (_: Exception) {
                textViewDate.text = transaction.date
            }
            val formattedAmount = currencyFormatter.format(transaction.amount)

            val amountText = "${formattedAmount} ${transaction.currencyCode}"
            if (transaction.transactionTypeId == 1) {
                textViewAmount.text = "+$amountText"
                textViewAmount.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
            } else if (transaction.transactionTypeId == 2) {
                textViewAmount.text = "-$amountText"
                textViewAmount.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
            } else {
                // Toast.makeText("hata",this, Toast.LENGTH_SHORT).show()
            }
        }
    }
}