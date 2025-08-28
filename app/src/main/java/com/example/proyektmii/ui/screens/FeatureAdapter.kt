package com.example.proyektmii.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R

class FeatureAdapter(
    private val items: List<FeatureItem>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<FeatureAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val text: TextView = view.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feature_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.text.text = item.text
        holder.itemView.setOnClickListener { onClick(item.type) }
    }

    override fun getItemCount(): Int = items.size
}
