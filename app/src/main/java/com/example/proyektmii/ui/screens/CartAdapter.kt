package com.example.proyektmii.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.MenuItem
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgCart)
        val name: TextView = v.findViewById(R.id.tvCartName)
        val price: TextView = v.findViewById(R.id.tvCartPrice)
        val btnDec: Button = v.findViewById(R.id.btnDec)
        val btnInc: Button = v.findViewById(R.id.btnInc)
        val tvQty: TextView = v.findViewById(R.id.tvQty)
    }

    private fun formatPrice(value: Int): String {
        val nf = NumberFormat.getInstance(Locale("in", "ID"))
        return "Rp ${nf.format(value)}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ci = items[position]
        holder.name.text = ci.menuItem.name
        holder.price.text = formatPrice(ci.menuItem.price)
        holder.tvQty.text = ci.quantity.toString()
        holder.img.setImageResource(ci.menuItem.imageRes ?: R.drawable.nasgor)

        holder.btnInc.setOnClickListener {
            val newQty = ci.quantity + 1
            items[position] = ci.copy(quantity = newQty)
            notifyItemChanged(position)
            onQuantityChanged(items[position], newQty)
        }

        holder.btnDec.setOnClickListener {
            val newQty = ci.quantity - 1
            if (newQty <= 0) {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
                onQuantityChanged(ci.copy(quantity = 0), 0)
            } else {
                items[position] = ci.copy(quantity = newQty)
                notifyItemChanged(position)
                onQuantityChanged(items[position], newQty)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}