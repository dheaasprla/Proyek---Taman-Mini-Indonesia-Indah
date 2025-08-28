package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var cartListView: ListView
    private lateinit var totalItemText: TextView
    private lateinit var totalPriceItems: TextView // Menggunakan ID yang benar
    private lateinit var totalPaymentText: TextView
    private lateinit var lanjutBayarButton: Button
    private lateinit var backButton: ImageButton

    private var cartItems = listOf<CartItem>()
    private val TAG = "CartActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        appPreferences = AppPreferences(this)

        try {
            cartListView = findViewById(R.id.cart_list_view)
            totalItemText = findViewById(R.id.total_item_text)
            totalPriceItems = findViewById(R.id.total_price_items) // Menggunakan ID yang benar
            totalPaymentText = findViewById(R.id.total_payment_text)
            lanjutBayarButton = findViewById(R.id.lanjut_bayar_button)
            backButton = findViewById(R.id.back_button_cart)

            backButton.setOnClickListener { onBackPressed() }

            lanjutBayarButton.setOnClickListener {
                val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
                if (totalPrice > 0) {
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("totalPrice", totalPrice)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            Toast.makeText(this, "Aplikasi mengalami masalah. Silakan coba lagi.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    private fun loadCartItems() {
        cartItems = appPreferences.getCartItems()
        if (cartItems.isNotEmpty()) {
            val adapter = CartListAdapter(cartItems)
            cartListView.adapter = adapter
        } else {
            cartListView.adapter = null
        }
        updateSummary()
    }

    private fun updateSummary() {
        val totalItems = cartItems.sumOf { it.quantity }
        val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }

        totalItemText.text = "Total Item (${totalItems})"
        totalPriceItems.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
        totalPaymentText.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"

        lanjutBayarButton.isEnabled = totalItems > 0
    }

    inner class CartListAdapter(private val items: List<CartItem>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_cart, parent, false)

            val item = items[position]

            val nameTextView: TextView = view.findViewById(R.id.cart_item_name)
            val priceTextView: TextView = view.findViewById(R.id.cart_item_price)
            val minusButton: Button = view.findViewById(R.id.minus_button)
            val plusButton: Button = view.findViewById(R.id.plus_button)
            val quantityTextView: TextView = view.findViewById(R.id.quantity_text)

            nameTextView.text = item.menuItem.name
            priceTextView.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(item.menuItem.price)}"
            quantityTextView.text = item.quantity.toString()

            minusButton.setOnClickListener {
                val newQuantity = item.quantity - 1
                val updatedItems = if (newQuantity > 0) {
                    items.map { if (it == item) it.copy(quantity = newQuantity) else it }
                } else {
                    items.filter { it != item }
                }
                appPreferences.saveCartItems(updatedItems)
                this@CartActivity.loadCartItems()
            }

            plusButton.setOnClickListener {
                val newQuantity = item.quantity + 1
                val updatedItems = items.map { if (it == item) it.copy(quantity = newQuantity) else it }
                appPreferences.saveCartItems(updatedItems)
                this@CartActivity.loadCartItems()
            }
            return view
        }
    }
}