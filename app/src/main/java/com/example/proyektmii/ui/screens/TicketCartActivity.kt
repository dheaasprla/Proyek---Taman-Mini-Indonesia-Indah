package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proyektmii.R
import com.example.proyektmii.data.TicketItem

class TicketCartActivity : AppCompatActivity() {

    private var ticketItem: TicketItem? = null
    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var layoutSummary: View
    private lateinit var tvTotalInfo: TextView
    private lateinit var btnProceed: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_cart)

        ticketItem = intent.getParcelableExtra("ticket") as? TicketItem

        val btnBack = findViewById<ImageButton>(R.id.btnBackTicket)
        btnBack.setOnClickListener { finish() }

        rv = findViewById(R.id.rvTicket)
        tvEmpty = findViewById(R.id.tvEmptyTicket)
        layoutSummary = findViewById(R.id.layoutSummaryTicket)
        tvTotalInfo = findViewById(R.id.tvTotalTicketInfo)
        btnProceed = findViewById(R.id.btnProceedTicketPayment)

        if (ticketItem == null) {
            tvEmpty.visibility = View.VISIBLE
            layoutSummary.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            layoutSummary.visibility = View.VISIBLE
            bindTicket()
        }

        btnProceed.setOnClickListener {
            val total = ticketItem?.quantity?.let { qty ->
                (ticketItem!!.destination.price * qty)
            } ?: 0
            // Lanjut ke PaymentActivity (reuse payment activity dari kantin)
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("total", total)
            // optionally pass ticket as cart
            startActivity(intent)
        }
    }

    private fun bindTicket() {
        ticketItem?.let { item ->
            rv.adapter = TicketAdapter(item) { newQty ->
                if (newQty <= 0) {
                    ticketItem = null
                    tvEmpty.visibility = View.VISIBLE
                    layoutSummary.visibility = View.GONE
                } else {
                    ticketItem = item.copy(quantity = newQty)
                    // rebind adapter with new quantity
                    rv.adapter = TicketAdapter(ticketItem!!) { q -> /* handle again */ }
                    val totalPrice = ticketItem!!.quantity * ticketItem!!.destination.price
                    tvTotalInfo.text = "Rp ${formatPrice(totalPrice)}"
                }
            }
            val totalPrice = item.quantity * item.destination.price
            tvTotalInfo.text = "Rp ${formatPrice(totalPrice)}"
        }
    }

    private fun formatPrice(v: Int): String =
        java.text.NumberFormat.getInstance(java.util.Locale("in", "ID")).format(v)
}