package com.aysusen.financetracker.view

import android.os.Bundle
import android.R
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
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

class IncomeFragment : Fragment() {
    sealed class TransactionState() {
        object Idle : TransactionState() // Boşta
        object Loading : TransactionState() // Yükleniyor
        object Success : TransactionState() // Başarılı
        data class Error(val message: String) : TransactionState() // Hatalı
    }

    private lateinit var binding: FragmentFormBinding
    private lateinit var buttonSave: Button
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextAmount: TextInputEditText
    private val currenciesViewModel: CurrenciesViewModel by activityViewModels()

    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val mainViewModel: CurrenciesViewModel by activityViewModels()
    private lateinit var appCompleteCurrencies: AutoCompleteTextView
    private var selectedCurrencyId: Int? = null
    private var fullCurrencyList: List<CurrencyResponse> = emptyList()

    private val _currencies = MutableStateFlow<List<CurrencyResponse>>(emptyList())
    val currencies: StateFlow<List<CurrencyResponse>> = _currencies

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState
    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions: StateFlow<List<TransactionResponse>> = _transactions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSave = binding.buttonSave
        editTextTitle = binding.editTextTitle
        editTextAmount = binding.editTextAmount
        appCompleteCurrencies = binding.autoCompleteCurrency

        observeCurrencies()
        setupCurrencySelectionListener()
        setupSaveButtonListener()
        observeTransactionState()
        currenciesViewModel.fetchCurrencies()
    }
    private fun observeCurrencies() {
        // Fragment'ta 'viewLifecycleOwner.lifecycleScope' kullanılır
        viewLifecycleOwner.lifecycleScope.launch {

            // 'mainViewModel' kullanılır (çünkü yukarıda öyle tanımladık)
            mainViewModel.currencies.collect { currencyList ->
                if (currencyList.isNotEmpty()) {
                    Log.d("IncomeFragment", "Kurlar alındı: ${currencyList.size} adet")

                    fullCurrencyList = currencyList
                    val currencyCodes = currencyList.map { it.code }

                    // 'requireContext()' Fragment'ta çalışır
                    val adapter = ArrayAdapter(requireContext(),
                        R.layout.simple_dropdown_item_1line,
                        currencyCodes)
                    binding.autoCompleteCurrency.setAdapter(adapter)
                }
            }
        }
    }
    private fun setupCurrencySelectionListener() {
        binding.autoCompleteCurrency.setOnItemClickListener { parent, view, position, id ->

            val selectedCode = parent.getItemAtPosition(position) as String

            val selectedCurrency = fullCurrencyList.find { it.code == selectedCode }

            selectedCurrencyId = selectedCurrency?.id

            // 4. (İÇERİ TAŞINDI) Loglamayı yap
            Log.d("IncomeFragment", "Seçilen Kur: $selectedCode, ID: $selectedCurrencyId")

            selectedCurrencyId = selectedCurrency?.id

            Log.d("IncomeFragment", "Seçilen Kur: $selectedCode, ID: $selectedCurrencyId")

            // TODO: Bu 'selectedCurrencyId' değişkenini 'Kaydet' butonuna bastığında
            // 'CreateTransactionCommand' için kullan.
        }
    }
    private fun setupSaveButtonListener() {
        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()?:0.0

            // Veri doğrulaması
            if (title.isBlank() || amount == 0.0 || selectedCurrencyId == null) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val typeId_Gelir = 1

            transactionViewModel.createTransaction(title, amount, selectedCurrencyId!!, typeId_Gelir)

            Log.d("IncomeFragment", "KAYDEDİLİYOR: $title, $amount, $selectedCurrencyId, $typeId_Gelir")
            Toast.makeText(requireContext(), "Kaydedildi!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun observeTransactionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            transactionViewModel.transactionState.collect { state ->
                when (state) {
                    is TransactionState.Loading -> {
                        // Kaydederken butonu kilitle
                        binding.buttonSave.isEnabled = false
                        Log.d("IncomeFragment", "Kaydediliyor...")
                    }
                    is TransactionState.Success -> {
                        Toast.makeText(requireContext(), "Gelir başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                        binding.buttonSave.isEnabled = true

                        // Formu temizle veya geri git (örn: findNavController().popBackStack())

                        // ViewModel'deki durumu sıfırla
                        currenciesViewModel.resetTransactionState()
                    }
                    is TransactionState.Error -> {
                        Toast.makeText(requireContext(), "Hata: ${state.message}", Toast.LENGTH_LONG).show()
                        binding.buttonSave.isEnabled = true
                        currenciesViewModel.resetTransactionState()
                    }
                    is TransactionState.Idle -> {
                        // Boşta
                        binding.buttonSave.isEnabled = true
                    }
                    else -> {

                    }
                }
            }
        }
    }
}
