package com.aysusen.financetracker.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aysusen.financetracker.adapter.RecyclerViewAdapter
import com.aysusen.financetracker.databinding.FragmentExtractAccountBinding
import com.aysusen.financetracker.viewModel.TransactionViewModel
import kotlinx.coroutines.launch

class ExtractAccountFragment : Fragment() {

    private var _binding: FragmentExtractAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: RecyclerViewAdapter
    private val transactionViewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtractAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTransactions()
        observeTransactionListState()
        transactionViewModel.fetchTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecyclerViewAdapter()
        binding.recyclerViewTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { transactions ->
                    transactionAdapter.submitList(transactions)
                    Log.d("ExtractAccountFragment", "Transactions updated: ${transactions.size}")
                }
            }
        }
    }

    private fun observeTransactionListState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactionListState.collect { state ->
                    when (state) {
                        is TransactionViewModel.TransactionListState.Idle -> {
                        }

                        is TransactionViewModel.TransactionListState.Loading -> {
                            Log.d("ExtractAccountFragment", "Loading transactions...")
                        }

                        is TransactionViewModel.TransactionListState.Success -> {
                            Log.d(
                                "ExtractAccountFragment",
                                "Transactions loaded: ${state.transactions.size}"
                            )
                        }

                        is TransactionViewModel.TransactionListState.Error -> {
                            showError(state.message)
                            Log.e("ExtractAccountFragment", "Error: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(
            requireContext(), "İşlemler yüklenemedi: $message", Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}