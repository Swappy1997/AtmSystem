package com.example.atmsystem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.atmsystem.R
import com.example.atmsystem.models.Denomination
import com.example.atmsystem.models.WithdrawalDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AtmViewModel : ViewModel() {


    private val _availableDenominations = MutableLiveData<List<Denomination>>(
        listOf(
            Denomination(2000, 0),
            Denomination(500, 0),
            Denomination(200, 0),
            Denomination(100, 0)
        )
    )

    val availableDenominations: LiveData<List<Denomination>> = _availableDenominations

    private val _withdrawnDetails = MutableLiveData<WithdrawalDetails>()
    val withdrawnDetails: LiveData<WithdrawalDetails> = _withdrawnDetails

    private val _transactionListLiveData = MutableLiveData<List<WithdrawalDetails>>()
    val transactionListLiveData: LiveData<List<WithdrawalDetails>> = _transactionListLiveData

    fun deposit(amount2000: Int, amount500: Int, amount200: Int, amount100: Int) {
        val updatedDenominations = _availableDenominations.value?.toMutableList() ?: mutableListOf()
        if (amount2000 < 0 || amount500 < 0 || amount200 < 0 || amount100 < 0) {
            invalidAmount("Invalid deposit amount")
            return
        }
        for (denomination in updatedDenominations) {
            when (denomination.value) {
                2000 -> {
                    denomination.count += amount2000
                }

                500 -> {
                    denomination.count += amount500
                }

                200 -> {
                    denomination.count += amount200
                }

                100 -> {
                    denomination.count += amount100
                }
            }
        }
        _availableDenominations.postValue(updatedDenominations)

    }


    fun withdraw(amount: Int) {
        val available = _availableDenominations.value ?: emptyList()
        val withdrawn = mutableListOf<Denomination>()

        if (amount <= 0) {
            invalidAmount("Invalid withdrawal amount")
            return
        }

        val totalAvailableAmount = available.sumBy { it.value * it.count }
        if (amount > totalAvailableAmount) {
            invalidAmount("Insufficient funds")
            return
        }

        var remainingAmount = amount

        val sortedDenominations = available.sortedByDescending { it.value }

        for (denomination in sortedDenominations) {
            if (remainingAmount > 0 && denomination.count > 0) {
                val count = remainingAmount / denomination.value
                val withdrawnCount = minOf(count, denomination.count)
                withdrawn.add(Denomination(denomination.value, withdrawnCount))
                remainingAmount -= withdrawnCount * denomination.value
            }
        }

        if (remainingAmount == 0) {
            val updatedDenominations = available.toMutableList()

            for (withdrawnDenomination in withdrawn) {
                val existingIndex =
                    updatedDenominations.indexOfFirst { it.value == withdrawnDenomination.value }

                if (existingIndex != -1) {
                    val existingDenomination = updatedDenominations[existingIndex]

                    if (existingDenomination.count < withdrawnDenomination.count) {
                        invalidAmount("Insufficient ${existingDenomination.value} denomination available")
                        return
                    }

                    updatedDenominations[existingIndex] =
                        existingDenomination.copy(count = existingDenomination.count - withdrawnDenomination.count)
                }
            }

            addWithdrawalTransactionDetails(withdrawn)
            _availableDenominations.postValue(updatedDenominations)
            _withdrawnDetails.postValue(WithdrawalDetails(null, withdrawn))
        } else {
            invalidAmount("Cannot withdraw requested amount with available denominations")
        }
    }


    fun addDepositTransactionDetails(depositedAmountsList: List<Pair<Int, Int>>) {
        val depositedDenominations =
            depositedAmountsList.map { (denominationValue, depositedAmount) ->
                Denomination(denominationValue, depositedAmount)
            }
        val newList = transactionListLiveData.value?.toMutableList() ?: mutableListOf()
        val timestamp = SimpleDateFormat(
            "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)",
            Locale.getDefault()
        ).format(Date())
        val backgroundColor = R.color.blue
        val transactionDetails =
            WithdrawalDetails(null, depositedDenominations, backgroundColor, timestamp)
        newList.add(transactionDetails)
        _transactionListLiveData.postValue(newList)
    }

    fun addWithdrawalTransactionDetails(withdrawnDenominations: List<Denomination>) {
        val newList = transactionListLiveData.value?.toMutableList() ?: mutableListOf()
        val timestamp = SimpleDateFormat(
            "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)",
            Locale.getDefault()
        ).format(Date())
        val backgroundColor = R.color.green  // Set your desired background color here
        val transactionDetails =
            WithdrawalDetails(null, withdrawnDenominations, backgroundColor, timestamp)
        newList.add(transactionDetails)
        _transactionListLiveData.postValue(newList)
    }


    fun invalidAmount(message: String) {
        val timestamp = SimpleDateFormat(
            "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)",
            Locale.getDefault()
        ).format(Date())
        val newList = transactionListLiveData.value?.toMutableList() ?: mutableListOf()
        val backgroundColor = R.color.red
        val transactionDetails =
            WithdrawalDetails(message, null, backgroundColor, timestamp)
        newList.add(transactionDetails)
        _transactionListLiveData.postValue(newList)
    }
}