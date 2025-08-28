package com.example.proyektmii.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R
import com.example.proyektmii.data.TicketItem
import java.text.NumberFormat
import java.util.*

class TicketAdapter(
    private val item: TicketItem,
    private val onQuantityChanged: (Int) -> Unit
) : RecyclerView.Adapter<TicketAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgTicket)
        val name: TextView = v.findViewById(R.id.tvTicketName)
        val price: TextView = v.findViewById(R.id.tvTicketPrice)
        val btnDec: Button = v.findViewById(R.id.btnTicketDec)
        val btnInc: Button = v.findViewById(R.id.btnTicketInc)
        val tvQty: TextView = v.findViewById(R.id.tvTicketQty)
    }

    private fun formatPrice(v: Int): String =
        "Rp ${NumberFormat.getInstance(Locale("in", "ID")).format(v)}"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.name.text = item.destination.name
        holder.price.text = formatPrice(item.destination.price)
        holder.tvQty.text = item.quantity.toString()
        holder.img.setImageResource(item.destination.imageRes ?: R.drawable.museum)

        holder.btnInc.setOnClickListener {
            onQuantityChanged(item.quantity + 1)
        }
        holder.btnDec.setOnClickListener {
            onQuantityChanged(item.quantity - 1)
        }
    }

    override fun getItemCount(): Int = 1
}