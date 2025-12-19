package com.aysusen.financetracker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aysusen.financetracker.databinding.ActivityMainBinding
import com.aysusen.financetracker.viewModel.CurrenciesViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrenciesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupObservers()
        viewModel.fetchCurrencies()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fabAdd.setOnClickListener {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.extractAccount, false).build()
            navController.navigate(R.id.addFinanceFragment, null, navOptions)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currencyState.collect { state ->
                    when (state) {
                        is CurrenciesViewModel.CurrencyState.Idle -> {
                            Log.d("MainActivity", "Currency state: Idle")
                        }

                        is CurrenciesViewModel.CurrencyState.Loading -> {
                            Log.d("MainActivity", "Currency state: Loading")
                        }

                        is CurrenciesViewModel.CurrencyState.Success -> {
                            Log.d("MainActivity", "Currencies loaded: ${state.currencies.size}")

                        }

                        is CurrenciesViewModel.CurrencyState.Error -> {
                            Log.e("MainActivity", "Error loading currencies: ${state.message}")
                            Toast.makeText(
                                this@MainActivity,
                                "Kurlar yüklenemedi: ${state.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}