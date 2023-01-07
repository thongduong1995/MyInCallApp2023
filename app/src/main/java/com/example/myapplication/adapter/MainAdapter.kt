package com.example.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class MainAdapter( val dataSet: List<NumberItem>) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
    init {
        dataSet.forEach { Log.i("thong", it.mainNumber) }
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainNum: TextView = view.findViewById<TextView>(R.id.main_number)
        val subNum: TextView = view.findViewById<TextView>(R.id.sub_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.number_pad_item, parent, false)
//        v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.WRAP_CONTENT)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mainNum.text = dataSet[position].mainNumber
        holder.subNum.text = dataSet[position].subNumber
//        holder.itemView.setOnClickListener {  }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}