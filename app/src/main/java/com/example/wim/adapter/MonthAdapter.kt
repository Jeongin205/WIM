package com.example.wim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wim.R
import com.example.wim.data.SpendingMonthData
import com.example.wim.databinding.ItemMonthBinding

class MonthAdapter(val data: ArrayList<SpendingMonthData>): RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {
    class MonthViewHolder(val binding: ItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        val holder = MonthViewHolder(ItemMonthBinding.bind(v))
        return holder
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.binding.data = data[position]
    }

    override fun getItemCount(): Int = data.size
}