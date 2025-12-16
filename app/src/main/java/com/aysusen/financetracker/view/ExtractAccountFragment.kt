package com.aysusen.financetracker.view

import RetrofitClient
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aysusen.financetracker.adapter.RecyclerViewAdapter
import com.aysusen.financetracker.databinding.FragmentExtractAccountBinding
import com.aysusen.financetracker.model.TransactionResponse
import com.aysusen.financetracker.viewModel.TransactionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExtractAccountFragment : Fragment() {
    private lateinit var binding: FragmentExtractAccountBinding
    private lateinit var transactionAdapter: RecyclerViewAdapter
    private val mainViewModel: TransactionViewModel by activityViewModels()
    private val _transactionState = MutableStateFlow<TransactionViewModel.TransactionState>(
        TransactionViewModel.TransactionState.Idle)
    val transactionState: StateFlow<TransactionViewModel.TransactionState> = _transactionState
    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions: StateFlow<List<TransactionResponse>> = _transactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExtractAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeTransactionList()
        fetchTransactions()
    }

    fun setupRecyclerView() {
        transactionAdapter = RecyclerViewAdapter() // Adapter'ınızı oluşturun
        binding.recyclerViewTransactions.apply {
            adapter = transactionAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun fetchTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getTransaction()
                if (response.isSuccessful) {
                    _transactionState.value = TransactionViewModel.TransactionState.Success
                    _transactions.value = response.body() ?: emptyList()
                } else {
                    _transactionState.value =
                        TransactionViewModel.TransactionState.Error("Sunucu hatası: ${response.code()}")
                }

            } catch (e: Exception) {
                _transactionState.value = TransactionViewModel.TransactionState.Error("Bağlantı hatası: ${e.message}")
            }
        }
    }

    fun observeTransactionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            transactions.collect { transactions ->
                transactionAdapter.submitList(transactions)
                Log.d(
                    "ExtractAccountFragment",
                    "Transaction Listesi Alındı: ${transactions.size} adet"
                )
            }
        }
    }
}