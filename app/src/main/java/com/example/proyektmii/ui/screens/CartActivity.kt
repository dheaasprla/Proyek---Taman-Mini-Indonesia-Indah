package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
// import com.example.proyektmii.data.CardData // tidak dipakai di file ini

class CartActivity : AppCompatActivity() {

    private val cartItems = mutableListOf<CartItem>()
    private lateinit var rv: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var tvTotalInfo: TextView
    private lateinit var layoutSummary: View
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // ambil extras (aman kalau null)
        val incoming = intent.getParcelableArrayListExtra<CartItem>("cart") ?: arrayListOf()
        if (incoming.isNotEmpty()) cartItems.addAll(incoming)

        rv = findViewById(R.id.rvCart)
        tvEmpty = findViewById(R.id.tvEmpty)
        layoutSummary = findViewById(R.id.layoutSummary)
        tvTotalInfo = findViewById(R.id.tvTotalInfoCart)
        val btnProceed = findViewById<Button>(R.id.btnProceedPayment)
        val btnBack = findViewById<ImageButton>(R.id.btnBackCart)

        // set LayoutManager supaya RecyclerView bekerja
        rv.layoutManager = LinearLayoutManager(this)

        adapter = CartAdapter(cartItems) { updatedItem, newQty ->
            // update current list: adapter sudah memperbarui internal list
            updateSummary()
        }
        rv.adapter = adapter

        btnBack.setOnClickListener { finish() }

        btnProceed.setOnClickListener {
            // hitung total dan start PaymentActivity
            val total = cartItems.sumOf { it.menuItem.price * it.quantity }
            // gunakan PaymentActivity milik app (pastikan kamu punya class PaymentActivity di package yang sama)
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("total", total)
            // pass cart jika diperlukan
            intent.putParcelableArrayListExtra("cart", ArrayList(cartItems))
            startActivity(intent)
        }

        updateSummary()
    }

    private fun updateSummary() {
        if (cartItems.isEmpty()) {
            layoutSummary.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            layoutSummary.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            val totalItems = cartItems.sumOf { it.quantity }
            val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
            tvTotalInfo.text = "($totalItems item) Rp ${formatPrice(totalPrice)}"
        }
    }

    private fun formatPrice(value: Int): String {
        return java.text.NumberFormat.getInstance(java.util.Locale("in", "ID")).format(value)
    }
}