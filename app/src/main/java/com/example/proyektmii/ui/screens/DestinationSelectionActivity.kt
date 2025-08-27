package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyektmii.R
import com.example.proyektmii.data.Destination
import com.example.proyektmii.data.TicketItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class DestinationSelectionActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var destinationGrid: GridView
    private lateinit var continueButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var titleTextView: TextView

    private var selectedTicket: TicketItem? = null
    private var isWahana: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_selection)

        appPreferences = AppPreferences(this)

        destinationGrid = findViewById(R.id.destination_grid)
        continueButton = findViewById(R.id.lanjut_ke_keranjang_button)
        backButton = findViewById(R.id.back_button_destinasi_selection)
        titleTextView = findViewById(R.id.destination_selection_title)

        isWahana = intent.getBooleanExtra("isWahana", false)
        val destinations = if (isWahana) {
            listOf(
                Destination("Kereta Gantung", 50000, R.drawable.keretagantung),
                Destination("Jagat Satwa Nusantara", 60000, R.drawable.jagat),
                Destination("Teater Keong Emas", 50000, R.drawable.keong),
                Destination("Skyworld Indonesia", 90000, R.drawable.sky),
                Destination("Desa Seni Ganara Art", 25000, R.drawable.ganara)
            )
        } else {
            listOf(
                Destination("Contemporary Art Gallery", 25000, R.drawable.cag),
                Destination("Bayt Al-Qur'an & Museum Istiqlal", 10000, R.drawable.istiqlal),
                Destination("Museum Prangko", 5000, R.drawable.prangko),
                Destination("Museum Keprajuritan", 5000, R.drawable.kepra),
                Destination("Museum Transportasi", 10000, R.drawable.transportasi),
                Destination("Museum Listrik & Energi Baru", 20000, R.drawable.le),
                Destination("Indonesia Science Center - PPIPTEK", 27500, R.drawable.science)
            )
        }

        titleTextView.text = if (isWahana) "Wahana & Rekreasi" else "Museum"

        selectedTicket = appPreferences.getTicketItem()

        val adapter = DestinationGridAdapter(destinations)
        destinationGrid.adapter = adapter

        continueButton.setOnClickListener {
            selectedTicket?.let { ticket ->
                appPreferences.saveTicketItem(ticket)
                val intent = Intent(this, TicketPaymentActivity::class.java)
                startActivity(intent)
            }
        }

        backButton.setOnClickListener { onBackPressed() }

        updateContinueButtonState()
    }

    private fun updateContinueButtonState() {
        continueButton.isEnabled = selectedTicket != null
    }

    inner class DestinationGridAdapter(private val destinations: List<Destination>) : BaseAdapter() {
        override fun getCount(): Int = destinations.size
        override fun getItem(position: Int): Any = destinations[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.grid_item_destination, parent, false)

            val destination = destinations[position]

            val imageView: ImageView = view.findViewById(R.id.destination_image)
            val nameTextView: TextView = view.findViewById(R.id.destination_name)
            val priceTextView: TextView = view.findViewById(R.id.destination_price)
            val addButton: Button = view.findViewById(R.id.add_button)
            val quantityLayout: LinearLayout = view.findViewById(R.id.quantity_control)
            val minusButton: Button = view.findViewById(R.id.minus_button)
            val plusButton: Button = view.findViewById(R.id.plus_button)
            val quantityTextView: TextView = view.findViewById(R.id.quantity_text)

            imageView.setImageResource(destination.imageRes ?: R.drawable.museum)
            nameTextView.text = destination.name
            priceTextView.text = "Tiket: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(destination.price)}"

            val currentQuantity = selectedTicket.takeIf { it?.destination == destination }?.quantity ?: 0
            if (currentQuantity > 0) {
                addButton.visibility = View.GONE
                quantityLayout.visibility = View.VISIBLE
                quantityTextView.text = currentQuantity.toString()
            } else {
                addButton.visibility = View.VISIBLE
                quantityLayout.visibility = View.GONE
            }

            addButton.setOnClickListener {
                selectedTicket = TicketItem(destination, 1)
                updateContinueButtonState()
                notifyDataSetChanged()
            }
            minusButton.setOnClickListener {
                if (selectedTicket != null && selectedTicket!!.quantity > 1) {
                    selectedTicket = selectedTicket?.copy(quantity = selectedTicket!!.quantity - 1)
                } else {
                    selectedTicket = null
                }
                updateContinueButtonState()
                notifyDataSetChanged()
            }
            plusButton.setOnClickListener {
                selectedTicket = selectedTicket?.copy(quantity = selectedTicket!!.quantity + 1)
                updateContinueButtonState()
                notifyDataSetChanged()
            }

            return view
        }
    }
}