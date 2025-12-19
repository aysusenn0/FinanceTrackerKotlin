package com.aysusen.financetracker.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aysusen.financetracker.databinding.FragmentAnalysisBinding
import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.model.TransactionResponse
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import com.aysusen.financetracker.viewModel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    private lateinit var pieChart: PieChart
    private var totalExpenseTRY: Float = 0.0f
    private var totalIncomeTRY: Float = 0.0f

    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val currenciesViewModel: CurrenciesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        pieChart = binding.pieChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()
        observeData()
        transactionViewModel.fetchTransactions()
        currenciesViewModel.fetchCurrencies()
    }

    private fun setupChart() {
        pieChart.apply {
            setNoDataText("Veri yükleniyor...")
            setUsePercentValues(false)
            description.isEnabled = false
            isRotationEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    transactionViewModel.transactions, currenciesViewModel.currencies
                ) { transactions, currencies ->
                    Pair(transactions, currencies)
                }.collect { (transactions, currencies) ->
                    if (transactions.isNotEmpty() && currencies.isNotEmpty()) {
                        updateChart(transactions, currencies)
                        Log.d(
                            "AnalysisFragment",
                            "Chart updated with ${transactions.size} transactions"
                        )
                    } else {
                        showEmptyState()
                        Log.d("AnalysisFragment", "Waiting for data...")
                    }
                }
            }
        }
    }

    private fun updateChart(
        transactions: List<TransactionResponse>, currencies: List<CurrencyResponse>
    ) {
        totalIncomeTRY = 0.0f
        totalExpenseTRY = 0.0f

        val rateMap = currencies.associate { it.code to it.rateToTRY }

        transactions.forEach { transaction ->
            val amount = transaction.amount.toFloat()
            val currencyCode = transaction.currencyCode
            val rate = if (currencyCode == "TRY") 1.0 else (rateMap[currencyCode] ?: 1.0)
            val amountInTRY = (amount * rate).toFloat()

            when (transaction.transactionTypeId) {
                1 -> totalIncomeTRY += amountInTRY // Gelir
                2 -> totalExpenseTRY += Math.abs(amountInTRY) // Gider
                else -> Log.e(
                    "AnalysisFragment",
                    "Invalid transactionTypeId: ${transaction.transactionTypeId}"
                )
            }
        }
        if (totalIncomeTRY == 0.0f && totalExpenseTRY == 0.0f) {
            showEmptyState()
            return
        }

        val entries = ArrayList<PieEntry>()
        if (totalIncomeTRY > 0) {
            entries.add(PieEntry(totalIncomeTRY, "Gelir"))
        }
        if (totalExpenseTRY > 0) {
            entries.add(PieEntry(totalExpenseTRY, "Gider"))
        }

        // Create dataset
        val dataSet = PieDataSet(entries, "Finansal Özet").apply {
            colors = getChartColors()
            valueTextSize = 16f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }

        // Update chart
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()

        updateSummaryTexts()
    }

    private fun updateSummaryTexts() {
        val formatter = java.text.DecimalFormat("#,##0.00")
        val symbols = java.text.DecimalFormatSymbols(java.util.Locale("tr", "TR"))
        formatter.decimalFormatSymbols = symbols

        binding.textViewIncome.text = "Gelir:\n${formatter.format(totalIncomeTRY)} TRY"
        binding.textViewExpense.text = "Gider:\n${formatter.format(totalExpenseTRY)} TRY"
    }

    private fun showEmptyState() {
        pieChart.clear()
        pieChart.setNoDataText("Henüz hiç Gelir veya Gider kaydı yok.")
        pieChart.invalidate()

        binding.textViewIncome.text = "Gelir:\n0.00 TRY"
        binding.textViewExpense.text = "Gider:\n0.00 TRY"
    }

    private fun getChartColors() = arrayListOf<Int>().apply {
        if (totalIncomeTRY > 0) {
            add(Color.parseColor("#74992c")) // Green for income
        }
        if (totalExpenseTRY > 0) {
            add(Color.parseColor("#CC0000")) // Red for expense
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}