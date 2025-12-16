package com.aysusen.financetracker.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aysusen.financetracker.model.CurrencyResponse
import com.aysusen.financetracker.model.TransactionResponse
import com.aysusen.financetracker.databinding.FragmentAnalysisBinding
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import com.aysusen.financetracker.viewModel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch

//transactionları listele (veriyi güncelle sadece amount ve gelir gider ayır)
//pie charta ekle

class AnalysisFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var pieChart: PieChart
    var totalExpenseTRY: Float = 0.0f
    var totalIncomeTRY: Float = 0.0f
    private val tViewModel: TransactionViewModel by activityViewModels()
    private val cViewModel: CurrenciesViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        pieChart = binding.pieChart
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTransaction()
        tViewModel.fetchTransactions()

    }

    fun updateChart(transactions: List<TransactionResponse>, currencies: List<CurrencyResponse>) {
        val rateMap = currencies.associate { it.code to it.rateToTRY }
        transactions.forEach {  transaction->
            val amount_ = transaction.amount.toFloat()
            val currencyCode = transaction.currencyCode

            val rate = if (currencyCode == "TRY") 1.0 else (rateMap[currencyCode] ?: 1.0)

            val amountInTRY = (amount_ * rate).toFloat()

            if (transaction.transactionTypeId == 1) { // Gelir
                totalIncomeTRY += amountInTRY
            } else if (transaction.transactionTypeId == 2) { // Gider
                totalExpenseTRY += Math.abs(amountInTRY)
            } else {
                Log.e(
                    "AnalysisFragment",
                    "Geçersiz transactionTypeId: ${transaction.transactionTypeId}"
                )
            }
        }
        val entries = ArrayList<PieEntry>()

        if (totalIncomeTRY == 0.0f && totalExpenseTRY == 0.0f) {
            pieChart.setNoDataText("Henüz hiç Gelir veya Gider kaydı yok.")
            pieChart.invalidate()
            return
        }
        if (totalIncomeTRY > 0) {
            entries.add(PieEntry(totalIncomeTRY, "Gelir"))
        }
        if (totalExpenseTRY > 0) {
            entries.add(PieEntry(totalExpenseTRY, "Gider"))
        }

        val colors = arrayListOf<Int>()

        val dataSet = PieDataSet(entries, "Harcamalar")

        dataSet.colors = getChartColors()


        dataSet.valueTextSize = 20f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        pieChart.data = data

        // Grafiği güncelle
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
        pieChart.isRotationEnabled = false

        val formatter = java.text.DecimalFormat("#,###.00")
        val symbols = java.text.DecimalFormatSymbols(java.util.Locale("tr", "TR"))
        formatter.decimalFormatSymbols = symbols

        binding.textViewIncome.text = "Gelir:\n" + "${formatter.format(totalIncomeTRY)} TRY"
        binding.textViewExpense.text = "Gider:\n" + "${formatter.format(totalExpenseTRY)} TRY"
    }

    private fun observeTransaction() {
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.flow.combine(
                tViewModel.transactions, cViewModel.currencies
            ) { transactions, currencies ->
                Pair(transactions, currencies)
            }.collect { (transactions, currencies) ->
                if (transactions.isNotEmpty() && currencies.isNotEmpty()) {
                    updateChart(transactions, currencies)
                    Log.d("Analysis fragment", "data is ready")
                } else {
                    Log.d("Analysis fragment", "data is not ready")
                }
            }
        }
    }

    private fun getChartColors()= arrayListOf<Int>().apply {
        if (totalIncomeTRY > 0) {
            add(Color.parseColor("#74992c"))
        }
        if (totalExpenseTRY > 0) {
            add(Color.parseColor("#CC0000"))
        }
    }
}
