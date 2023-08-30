package com.example.atmsystem

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atmsystem.adapters.TransactionAdapter

import com.example.atmsystem.databinding.ActivityMainBinding
import com.example.atmsystem.models.Denomination
import com.example.atmsystem.models.Transaction
import com.example.atmsystem.models.WithdrawalDetails
import com.example.atmsystem.viewmodels.AtmViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: AtmViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TransactionAdapter

    private val transactionDetailsList = mutableListOf<WithdrawalDetails>()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AtmViewModel::class.java]
        adapter = TransactionAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        setupObservers()

        binding.depoite.setOnClickListener {

            val depositAmount2000 = binding.editCount2000.text.toString().toIntOrNull() ?: 0
            val depositAmount500 = binding.editCount500.text.toString().toIntOrNull() ?: 0
            val depositAmount200 = binding.editCount200.text.toString().toIntOrNull() ?: 0
            val depositAmount100 = binding.editCount100.text.toString().toIntOrNull() ?: 0

            viewModel.deposit(
                depositAmount2000,
                depositAmount500,
                depositAmount200,
                depositAmount100
            )

            val depositedAmountsList = listOf(
                Pair(2000, depositAmount2000),
                Pair(500, depositAmount500),
                Pair(200, depositAmount200),
                Pair(100, depositAmount100)
            )
            viewModel.addDepositTransactionDetails(depositedAmountsList)

            adapter.updateList(transactionDetailsList)
            binding.editCount2000.text.clear()
            binding.editCount500.text.clear()
            binding.editCount200.text.clear()
            binding.editCount100.text.clear()

        }

        binding.withdrawButton.setOnClickListener {
            val withdrawalAmount = binding.withdrawAmount.text.toString().toIntOrNull() ?: 0
            viewModel.withdraw(withdrawalAmount)
            binding.withdrawAmount.text.clear()
        }
    }

    private fun setupObservers() {
        viewModel.availableDenominations.observe(this) { available ->

            val denomination2000 = available.find { it.value == 2000 }
            val denomination500 = available.find { it.value == 500 }
            val denomination200 = available.find { it.value == 200 }
            val denomination100 = available.find { it.value == 100 }


            binding.count2000.text = denomination2000?.count.toString()
            binding.count500.text = denomination500?.count.toString()
            binding.count200.text = denomination200?.count.toString()
            binding.count100.text = denomination100?.count.toString()


            val totalSum = available.sumBy { it.value * it.count }
            binding.totalsum.text = totalSum.toString()
        }

        viewModel.transactionListLiveData.observe(this) { updatedList ->
            adapter.updateList(updatedList)
            adapter.notifyDataSetChanged()
        }

    }
}