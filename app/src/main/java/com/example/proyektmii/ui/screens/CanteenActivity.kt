package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.MenuItem
import java.text.NumberFormat
import java.util.*

class CanteenActivity : AppCompatActivity() {

    private val cartItems = mutableListOf<CartItem>()
    private lateinit var adapter: MenuAdapter

    // view refs used across methods
    private lateinit var cartSummary: View
    private lateinit var tvTotalInfo: TextView
    private lateinit var btnPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canteen)

        val menuItems = listOf(
            MenuItem("Nasi Goreng", 20000, R.drawable.nasgor),
            MenuItem("Mie Ayam", 15000, R.drawable.mieayam),
            MenuItem("Es Jeruk", 8000, R.drawable.esjeruk),
            MenuItem("Teh Manis", 5000, R.drawable.esteh)
        )

        val rv = findViewById<RecyclerView>(R.id.rvMenu)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.setHasFixedSize(true)

        // initialize view refs
        cartSummary = findViewById(R.id.cartSummary)
        tvTotalInfo = findViewById(R.id.tvTotalInfo)
        btnPay = findViewById(R.id.btnPay)

        adapter = MenuAdapter(menuItems, cartItems) { updatedCart ->
            updateCartSummary(updatedCart)
        }
        rv.adapter = adapter

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnPay.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // lanjut ke CartActivity (kirim cart)
                val intent = Intent(this, CartActivity::class.java)
                intent.putParcelableArrayListExtra("cart", ArrayList(cartItems))
                startActivity(intent)
            }
        }

        // inisialisasi (kosong)
        updateCartSummary(cartItems)
    }

    private fun formatPrice(value: Int): String {
        val nf = NumberFormat.getInstance(Locale("in", "ID"))
        return "Rp ${nf.format(value)}"
    }

    private fun updateCartSummary(updatedCart: List<CartItem>) {
        if (updatedCart.isEmpty()) {
            cartSummary.visibility = View.GONE
            btnPay.isEnabled = false
            tvTotalInfo.text = ""
        } else {
            cartSummary.visibility = View.VISIBLE
            btnPay.isEnabled = true
            val totalItems = updatedCart.sumOf { it.quantity }
            val totalPrice = updatedCart.sumOf { it.menuItem.price * it.quantity }
            tvTotalInfo.text = "($totalItems item) ${formatPrice(totalPrice)}"
        }
    }
}