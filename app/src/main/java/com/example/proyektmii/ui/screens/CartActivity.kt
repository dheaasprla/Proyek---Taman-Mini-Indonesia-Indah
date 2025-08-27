// app/src/main/java/com/example/proyektmii/ui/screens/CartActivity.kt
package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.MenuItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var cartListView: ListView
    private lateinit var totalItemText: TextView
    private lateinit var totalPaymentText: TextView
    private lateinit var lanjutBayarButton: Button
    private lateinit var backButton: ImageButton

    private var cartItems = listOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        appPreferences = AppPreferences(this)

        cartListView = findViewById(R.id.cart_list_view)
        totalItemText = findViewById(R.id.total_item_text)
        totalPaymentText = findViewById(R.id.total_payment_text)
        lanjutBayarButton = findViewById(R.id.lanjut_bayar_button)
        backButton = findViewById(R.id.back_button_cart)

        backButton.setOnClickListener { onBackPressed() }

        lanjutBayarButton.setOnClickListener {
            val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("totalPrice", totalPrice)
            startActivity(intent)
        }

        loadCartItems()
    }

    private fun loadCartItems() {
        cartItems = appPreferences.getCartItems()
        if (cartItems.isNotEmpty()) {
            val adapter = CartListAdapter(cartItems)
            cartListView.adapter = adapter
        }
        updateSummary()
    }

    private fun getImageResource(menuName: String): Int {
        return when (menuName) {
            "Nasi Goreng" -> R.drawable.nasgor
            "Mie Ayam" -> R.drawable.mieayam
            "Es Jeruk" -> R.drawable.esjeruk
            "Teh manis" -> R.drawable.esteh
            else -> R.drawable.nasgor
        }
    }

    private fun updateSummary() {
        val totalItems = cartItems.sumOf { it.quantity }
        val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
        totalItemText.text = "Total Item (${totalItems})"
        totalPaymentText.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
        lanjutBayarButton.isEnabled = totalItems > 0
    }

    inner class CartListAdapter(private val items: List<CartItem>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Perbaikan: Panggil LayoutInflater dengan cara yang spesifik
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_cart, parent, false)

            val item = items[position]

            // Perbaikan: Ganti nama ID agar sesuai dengan list_item_cart.xml
            val imageView: ImageView = view.findViewById(R.id.cart_item_image)
            val nameTextView: TextView = view.findViewById(R.id.cart_item_name)
            val priceTextView: TextView = view.findViewById(R.id.cart_item_price)
            val minusButton: Button = view.findViewById(R.id.minus_button)
            val plusButton: Button = view.findViewById(R.id.plus_button)
            val quantityTextView: TextView = view.findViewById(R.id.quantity_text)

            imageView.setImageResource(getImageResource(item.menuItem.name))
            nameTextView.text = item.menuItem.name
            priceTextView.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(item.menuItem.price)}"
            quantityTextView.text = item.quantity.toString()

            minusButton.setOnClickListener {
                if (item.quantity > 1) {
                    val newItems = items.map { if (it == item) it.copy(quantity = it.quantity - 1) else it }
                    cartItems = newItems
                } else {
                    cartItems = items.filter { it != item }
                }
                appPreferences.saveCartItems(cartItems)
                loadCartItems()
            }

            plusButton.setOnClickListener {
                val newItems = items.map { if (it == item) it.copy(quantity = it.quantity + 1) else it }
                cartItems = newItems
                appPreferences.saveCartItems(cartItems)
                loadCartItems()
            }

            return view
        }
    }
}