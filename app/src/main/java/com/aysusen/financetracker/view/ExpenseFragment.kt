package com.aysusen.financetracker.view

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.model.TransactionResponse
import com.aysusen.financetracker.databinding.FragmentFormBinding
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import com.aysusen.financetracker.viewModel.TransactionViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ExpenseFragment : Fragment() {
    sealed class TransactionState() {
        object Idle : TransactionState() // Boşta
        object Loading : TransactionState() // Yükleniyor
        object Success : TransactionState() // Başarılı
        data class Error(val message: String) : TransactionState() // Hatalı
    }
    private lateinit var binding: FragmentFormBinding
    private lateinit var buttonSave : Button
    private lateinit var editTextTitle : TextInputEditText
    private lateinit var editTextAmount : TextInputEditText
    //private lateinit var date: TextInputEditText
    private val mainViewModel: CurrenciesViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val currenciesViewModel: CurrenciesViewModel by activityViewModels()
    private var selectedCurrencyId: Int? = null
    private var fullCurrencyList: List<CurrencyResponse> = emptyList()
    private lateinit var appCompleteCurrencies: AutoCompleteTextView
    private val _transactionState = MutableStateFlow<TransactionViewModel.TransactionState>(
        TransactionViewModel.TransactionState.Idle)
    val transactionState: StateFlow<TransactionViewModel.TransactionState> = _transactionState
    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions: StateFlow<List<TransactionResponse>> = _transactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextTitle = binding.editTextTitle
        editTextAmount = binding.editTextAmount
        appCompleteCurrencies = binding.autoCompleteCurrency
        buttonSave=binding.buttonSave

        observeCurrencies()
        setupCurrencySelectionListener()
        setupSaveButtonListener()
        observeTransactionState()
    }
    private fun observeTransactionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.currencies.collect { state ->
                when (state) {
                    is TransactionViewModel.TransactionState.Loading -> {
                        // TODO: Bir ProgressBar gösterebilirsin
                        binding.buttonSave.isEnabled = false // Butonu kilitle
                        Log.d("IncomeFragment", "Kaydediliyor...")
                    }

                    is TransactionViewModel.TransactionState.Success -> {
                        // BAŞARILI!
                        Toast.makeText(
                            requireContext(),
                            "Gelir başarıyla kaydedildi!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.buttonSave.isEnabled = true

                        // TODO: Formu temizle veya bir önceki ekrana dön
                        // findNavController().popBackStack() // Örn: Geri dön

                        // ViewModel'deki durumu sıfırla
                        resetTransactionState()
                    }

                    is TransactionViewModel.TransactionState.Error -> {
                        // HATA!
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.buttonSave.isEnabled = true // Butonun kilidini aç

                        resetTransactionState()
                    }

                    is TransactionViewModel.TransactionState.Idle -> {
                        binding.buttonSave.isEnabled = true
                    }
                }
            }
        }
    }

    private fun observeCurrencies() {
        viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.currencies.collect { currencyList ->
                    if (currencyList.isNotEmpty()) {
                        Log.d("IncomeFragment", "Kurlar alındı: ${currencyList.size} adet")

                        fullCurrencyList = currencyList
                        val currencyCodes = currencyList.map { it.code }

                        // 'requireContext()' Fragment'ta çalışır
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_dropdown_item_1line,
                            currencyCodes
                        )
                        binding.autoCompleteCurrency.setAdapter(adapter)
                    }
                }
            }
        }

    private fun setupCurrencySelectionListener() {
        binding.autoCompleteCurrency.setOnItemClickListener { parent, view, position, id ->
            // 1. Seçilen kodu al
            val selectedCode = parent.getItemAtPosition(position) as String

            // 2. Koda göre tam objeyi bul
            val selectedCurrency = fullCurrencyList.find { it.code == selectedCode }

            selectedCurrencyId = selectedCurrency?.id

            // 4. (İÇERİ TAŞINDI) Loglamayı yap
            Log.d("IncomeFragment", "Seçilen Kur: $selectedCode, ID: $selectedCurrencyId")

            selectedCurrencyId = selectedCurrency?.id

            Log.d("IncomeFragment", "Seçilen Kur: $selectedCode, ID: $selectedCurrencyId")


        }
    }


    private fun setupSaveButtonListener() {
        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()

            if (title.isBlank() || amount == null || selectedCurrencyId == null) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val typeId_Gider = 2


            transactionViewModel.createTransaction(title, amount, selectedCurrencyId!!, typeId_Gider)

            Log.d("IncomeFragment", "KAYDEDİLİYOR: $title, $amount, $selectedCurrencyId, $typeId_Gider")
            Toast.makeText(requireContext(), "Kaydedildi!", Toast.LENGTH_SHORT).show()
        }
    }
    fun resetTransactionState() {
        _transactionState.value = TransactionViewModel.TransactionState.Idle
    }
}