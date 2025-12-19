package com.aysusen.financetracker.view

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aysusen.financetracker.databinding.FragmentFormBinding
import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import com.aysusen.financetracker.viewModel.TransactionViewModel
import kotlinx.coroutines.launch

class ExpenseFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private val currenciesViewModel: CurrenciesViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private var selectedCurrencyId: Int? = null
    private var fullCurrencyList: List<CurrencyResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()

        // Fetch currencies if not already loaded
        currenciesViewModel.fetchCurrencies()
    }

    private fun setupObservers() {
        // Observe currencies
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currenciesViewModel.currencies.collect { currencyList ->
                    if (currencyList.isNotEmpty()) {
                        Log.d("ExpenseFragment", "Kurlar alındı: ${currencyList.size} adet")
                        updateCurrencyDropdown(currencyList)
                    }
                }
            }
        }

        // Observe transaction state
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactionState.collect { state ->
                    handleTransactionState(state)
                }
            }
        }
    }

    private fun updateCurrencyDropdown(currencyList: List<CurrencyResponse>) {
        fullCurrencyList = currencyList
        val currencyCodes = currencyList.map { it.code }

        val adapter = ArrayAdapter(
            requireContext(), R.layout.simple_dropdown_item_1line, currencyCodes
        )
        binding.autoCompleteCurrency.setAdapter(adapter)
    }

    private fun handleTransactionState(state: TransactionViewModel.TransactionState) {
        when (state) {
            is TransactionViewModel.TransactionState.Idle -> {
                binding.buttonSave.isEnabled = true
            }

            is TransactionViewModel.TransactionState.Loading -> {
                binding.buttonSave.isEnabled = false
                Log.d("ExpenseFragment", "Kaydediliyor...")
            }

            is TransactionViewModel.TransactionState.Success -> {
                Toast.makeText(
                    requireContext(), state.message, Toast.LENGTH_SHORT
                ).show()
                binding.buttonSave.isEnabled = true
                clearForm()
                transactionViewModel.resetTransactionState()
            }

            is TransactionViewModel.TransactionState.Error -> {
                Toast.makeText(
                    requireContext(), "Hata: ${state.message}", Toast.LENGTH_LONG
                ).show()
                binding.buttonSave.isEnabled = true
                transactionViewModel.resetTransactionState()
            }
        }
    }

    private fun setupListeners() {
        binding.autoCompleteCurrency.setOnItemClickListener { parent, _, position, _ ->
            val selectedCode = parent.getItemAtPosition(position) as String
            val selectedCurrency = fullCurrencyList.find { it.code == selectedCode }
            selectedCurrencyId = selectedCurrency?.id
            Log.d("ExpenseFragment", "Seçilen Kur: $selectedCode, ID: $selectedCurrencyId")
        }

        binding.buttonSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val title = binding.editTextTitle.text.toString()
        val amount = binding.editTextAmount.text.toString().toDoubleOrNull()

        // Validation
        if (title.isBlank()) {
            Toast.makeText(requireContext(), "Lütfen başlık girin", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Lütfen geçerli bir tutar girin", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedCurrencyId == null) {
            Toast.makeText(requireContext(), "Lütfen para birimi seçin", Toast.LENGTH_SHORT).show()
            return
        }

        val typeIDGider = 2
        transactionViewModel.createTransaction(
            title = title,
            amount = amount,
            currencyId = selectedCurrencyId!!,
            transactionTypeId = typeIDGider
        )

        Log.d("ExpenseFragment", "Kaydediliyor: $title, $amount, $selectedCurrencyId")
    }

    private fun clearForm() {
        binding.editTextTitle.text?.clear()
        binding.editTextAmount.text?.clear()
        binding.autoCompleteCurrency.text.clear()
        selectedCurrencyId = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}