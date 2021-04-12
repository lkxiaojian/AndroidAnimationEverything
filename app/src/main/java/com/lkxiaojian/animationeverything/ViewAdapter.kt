package com.lkxiaojian.animationeverything

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 *create_time : 21-4-12 下午1:46
 *author: lk
 *description： ViewAdapter
 */
class ViewAdapter(list: List<String>) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {
    private val datas = list

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text:TextView=view.findViewById(R.id.atv_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text=datas[position]
        holder.text.setOnClickListener {
            val context = holder.text.context
            val intent = Intent(context, ViewActivity::class.java)
            intent.putExtra("type",datas[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = datas.size
}