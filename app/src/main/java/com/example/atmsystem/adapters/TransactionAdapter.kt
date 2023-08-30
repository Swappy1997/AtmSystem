package com.example.atmsystem.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.atmsystem.R
import com.example.atmsystem.databinding.CustomTransactionsBinding
import com.example.atmsystem.models.WithdrawalDetails
import com.example.atmsystem.viewmodels.AtmViewModel

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactionList: List<WithdrawalDetails> = emptyList()
    private var validationMessage: String? = null

    fun updateList(newList: List<WithdrawalDetails>) {
        transactionList = newList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CustomTransactionsBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transactionDetails = transactionList[position]
        holder.bind(transactionDetails)
    }

    override fun getItemCount(): Int = transactionList.size

    inner class TransactionViewHolder(private val binding: CustomTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transactionDetails: WithdrawalDetails) {
            val formattedDenominations = transactionDetails.denominations?.joinToString(" ") {
                "${it.value}:${it.count}"
            }
            if (transactionDetails.invalidMessage == null) {
                binding.withdrawDetails.text = formattedDenominations
                binding.timestamp.text = transactionDetails.timestamp
                if (transactionDetails.backgroundColor != null) {
                    binding.cardview.setCardBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            transactionDetails.backgroundColor
                        )
                    )
                } else {
                    // Set a default background color if no specific color is provided
                    binding.cardview.setCardBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.white
                        )
                    )
                }
            } else {
                binding.withdrawDetails.text = transactionDetails.invalidMessage
                binding.timestamp.text = transactionDetails.timestamp
                if (transactionDetails.backgroundColor != null) {
                    binding.cardview.setCardBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            transactionDetails.backgroundColor
                        )
                    )
                }
            }


        }
    }

}