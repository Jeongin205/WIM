package com.example.wim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wim.R
import com.example.wim.data.SpendingData
import com.example.wim.databinding.ItemMainBinding

class MainAdapter(val data: ArrayList<SpendingData>): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    class MainViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        val holder = MainViewHolder(ItemMainBinding.bind(v))
        return holder
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.binding.data = data[position]
    }

    override fun getItemCount(): Int = data.size
}