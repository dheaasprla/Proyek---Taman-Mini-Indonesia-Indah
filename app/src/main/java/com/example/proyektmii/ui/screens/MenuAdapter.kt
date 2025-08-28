package com.example.proyektmii.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.MenuItem
import java.text.NumberFormat
import java.util.*

class MenuAdapter(
    private val items: List<MenuItem>,
    private val cartItems: MutableList<CartItem>,
    private val onCartUpdated: (List<CartItem>) -> Unit
) : RecyclerView.Adapter<MenuAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgMenu)
        val name: TextView = view.findViewById(R.id.tvMenuName)
        val price: TextView = view.findViewById(R.id.tvMenuPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val qtyControls: View = view.findViewById(R.id.qtyControls)
        val btnDecrease: Button = view.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = view.findViewById(R.id.btnIncrease)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return VH(v)
    }

    private fun formatPrice(value: Int): String {
        val nf = NumberFormat.getInstance(Locale("in", "ID"))
        return "Rp ${nf.format(value)}"
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.price.text = formatPrice(item.price)

        // safe image resource (fallback)
        val imageRes = try {
            item.imageRes
        } catch (e: Exception) {
            R.drawable.nasgor
        }

        // Glide request options -> limit decode size to avoid OOM on low-memory devices
        val req = RequestOptions()
            .placeholder(getPlaceholder(holder))
            .error(getPlaceholder(holder))
            // override ukuran sesuai tampilan item (px). Kurangi kalau device sangat kecil.
            .override(400, 240)
            .centerCrop()

        Glide.with(holder.img.context)
            .load(imageRes)
            .apply(req)
            .into(holder.img)

        val existing = cartItems.find { it.menuItem.name == item.name }
        val qty = existing?.quantity ?: 0

        // show/hide controls
        if (qty > 0) {
            holder.btnAdd.visibility = View.GONE
            holder.qtyControls.visibility = View.VISIBLE
            holder.tvQuantity.text = qty.toString()
        } else {
            holder.btnAdd.visibility = View.VISIBLE
            holder.qtyControls.visibility = View.GONE
        }

        holder.btnAdd.setOnClickListener {
            // add with qty = 1
            cartItems.add(CartItem(item, 1))
            notifyItemChanged(position)
            onCartUpdated(cartItems)
        }

        holder.btnIncrease.setOnClickListener {
            val idx = cartItems.indexOfFirst { it.menuItem.name == item.name }
            if (idx >= 0) {
                val ci = cartItems[idx]
                cartItems[idx] = ci.copy(quantity = ci.quantity + 1)
                notifyItemChanged(position)
                onCartUpdated(cartItems)
            }
        }

        holder.btnDecrease.setOnClickListener {
            val idx = cartItems.indexOfFirst { it.menuItem.name == item.name }
            if (idx >= 0) {
                val ci = cartItems[idx]
                if (ci.quantity > 1) {
                    cartItems[idx] = ci.copy(quantity = ci.quantity - 1)
                } else {
                    cartItems.removeAt(idx)
                }
                notifyItemChanged(position)
                onCartUpdated(cartItems)
            }
        }
    }

    private fun getPlaceholder(holder: VH): Int {
        // gunakan placeholder kalau ada, kalau tidak, fallback ke nasgor
        return try {
            // coba placeholder resource id jika ada
            R.drawable.ic_placeholder
        } catch (e: Exception) {
            R.drawable.nasgor
        }
    }

    override fun getItemCount(): Int = items.size
}