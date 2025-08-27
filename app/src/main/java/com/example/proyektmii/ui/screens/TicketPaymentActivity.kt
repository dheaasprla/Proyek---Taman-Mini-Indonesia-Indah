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
import com.example.proyektmii.data.TicketItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class TicketPaymentActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var ticketListView: ListView
    private lateinit var totalTicketText: TextView
    private lateinit var totalPaymentText: TextView
    private lateinit var payButton: Button
    private lateinit var backButton: ImageButton

    private var ticketItem: TicketItem? = null
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_cart)

        appPreferences = AppPreferences(this)

        ticketListView = findViewById(R.id.ticket_list_view)
        totalTicketText = findViewById(R.id.total_ticket_text)
        totalPaymentText = findViewById(R.id.total_payment_text)
        payButton = findViewById(R.id.lanjut_bayar_button)
        backButton = findViewById(R.id.back_button_ticket_cart)

        backButton.setOnClickListener { onBackPressed() }

        payButton.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("totalPrice", totalPrice)
            startActivity(intent)
        }

        loadTicketData()
    }

    private fun loadTicketData() {
        ticketItem = appPreferences.getTicketItem()
        updateUI()
    }

    private fun updateUI() {
        ticketItem?.let {
            val items = listOf(it)
            val adapter = TicketListAdapter(items)
            ticketListView.adapter = adapter

            totalPrice = it.destination.price * it.quantity
            totalTicketText.text = "Total Tiket (${it.quantity})"
            totalPaymentText.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
            payButton.isEnabled = true
        } ?: run {
            totalPrice = 0
            totalTicketText.text = "Total Tiket (0)"
            totalPaymentText.text = "Rp 0"
            payButton.isEnabled = false
        }
    }

    inner class TicketListAdapter(private val items: List<TicketItem>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_ticket_cart, parent, false)

            val item = items[position]

            val imageView = view.findViewById<ImageView>(R.id.ticket_image)
            val nameTextView = view.findViewById<TextView>(R.id.ticket_name)
            val priceTextView = view.findViewById<TextView>(R.id.ticket_price_per_item)
            val minusButton = view.findViewById<Button>(R.id.minus_button)
            val plusButton = view.findViewById<Button>(R.id.plus_button)
            val quantityTextView = view.findViewById<TextView>(R.id.quantity_text)

            imageView.setImageResource(item.destination.imageRes ?: R.drawable.museum)
            nameTextView.text = item.destination.name
            priceTextView.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(item.destination.price)}"
            quantityTextView.text = item.quantity.toString()

            minusButton.setOnClickListener {
                if (item.quantity > 1) {
                    val newQuantity = item.quantity - 1
                    appPreferences.saveTicketItem(item.copy(quantity = newQuantity))
                } else {
                    appPreferences.saveTicketItem(null)
                }
                loadTicketData()
            }

            plusButton.setOnClickListener {
                val newQuantity = item.quantity + 1
                appPreferences.saveTicketItem(item.copy(quantity = newQuantity))
                loadTicketData()
            }

            return view
        }
    }
}