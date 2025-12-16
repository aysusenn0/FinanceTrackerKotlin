package com.aysusen.financetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aysusen.financetracker.databinding.ActivityMainBinding
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrenciesViewModel by viewModels()
    //private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel.fetchCurrencies()
        observeCurrencies_original()
        observeErrors()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.fabAdd.setOnClickListener {
            val navOptions = NavOptions.Builder()
                // nav_graph'ın başlangıç hedefine (ExtractAccountFragment) kadar geri dön
                .setPopUpTo(R.id.extractAccount, false).build()

            // Navigasyonu bu özel kurallarla yap
            navController.navigate(R.id.addFinanceFragment, null, navOptions)
        }
    }

    private fun observeCurrencies_original() {
        lifecycleScope.launch() {
            viewModel.currencies.collect { currencyList ->
                if (currencyList.isNotEmpty()) {
                    // MainActivity'nin tek işi bu olmalı:
                    Log.d("MainActivity", "Kurlar başarıyla çekildi: ${currencyList.size}")
                }
            }
        }
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let { errorMessage ->
                    // ŞİMDİLİK: Hatayı Logcat'e basalım
                    Log.e("MainActivity", "HATA: $errorMessage")

                }
            }
        }
    }

}