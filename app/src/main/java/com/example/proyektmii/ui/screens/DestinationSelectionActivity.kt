package com.example.proyektmii.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    private val TAG = "DestinationSelection"
    private lateinit var destinationAdapter: DestinationGridAdapter

    private val wahanaDestinations = listOf(
        Destination("Kereta Gantung", 50000, null),
        Destination("Jagat Satwa Nusantara", 60000, null),
        Destination("Teater Keong Emas", 50000, null),
        Destination("Skyworld Indonesia", 90000, null),
        Destination("Desa Seni Ganara Art", 25000, null)
    )

    private val museumDestinations = listOf(
        Destination("Contemporary Art Gallery", 25000, null),
        Destination("Bayt Al-Qur'an & Museum Istiqlal", 10000, null),
        Destination("Museum Prangko", 5000, null),
        Destination("Museum Keprajuritan", 5000, null),
        Destination("Museum Transportasi", 10000, null),
        Destination("Museum Listrik & Energi Baru", 20000, null),
        Destination("Indonesia Science Center - PPIPTEK", 27500, null)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_selection)
        Log.d(TAG, "onCreate: DestinationSelectionActivity started.")

        appPreferences = AppPreferences(this)
        destinationGrid = findViewById(R.id.destination_grid)
        continueButton = findViewById(R.id.lanjut_ke_keranjang_button)
        backButton = findViewById(R.id.back_button_destinasi_selection)
        titleTextView = findViewById(R.id.destination_selection_title)

        isWahana = intent.getBooleanExtra("isWahana", false)
        val destinations = if (isWahana) wahanaDestinations else museumDestinations

        titleTextView.text = if (isWahana) "Wahana & Rekreasi" else "Museum"

        destinationAdapter = DestinationGridAdapter(destinations)
        destinationGrid.adapter = destinationAdapter

        continueButton.setOnClickListener {
            if (selectedTicket != null) {
                appPreferences.saveTicketItem(selectedTicket!!)
                val intent = Intent(this, TicketPaymentActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan pilih tiket terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        selectedTicket = appPreferences.getTicketItem()
        updateContinueButtonState()
        destinationAdapter.notifyDataSetChanged()
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

            val nameTextView: TextView = view.findViewById(R.id.destination_name)
            val priceTextView: TextView = view.findViewById(R.id.destination_price)
            val addButton: Button = view.findViewById(R.id.add_button)
            val quantityLayout: LinearLayout = view.findViewById(R.id.quantity_control)
            val minusButton: Button = view.findViewById(R.id.minus_button)
            val plusButton: Button = view.findViewById(R.id.plus_button)
            val quantityTextView: TextView = view.findViewById(R.id.quantity_text)

            nameTextView.text = destination.name
            priceTextView.text = "Tiket: Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(destination.price)}"

            val isSelected = selectedTicket?.destination?.name == destination.name
            val currentQuantity = if (isSelected) selectedTicket?.quantity ?: 0 else 0

            if (currentQuantity > 0) {
                addButton.visibility = View.GONE
                quantityLayout.visibility = View.VISIBLE
                quantityTextView.text = currentQuantity.toString()
            } else {
                addButton.visibility = View.VISIBLE
                quantityLayout.visibility = View.GONE
            }

            addButton.setOnClickListener {
                onTicketUpdate(TicketItem(destination, 1))
            }
            minusButton.setOnClickListener {
                if (currentQuantity > 1) {
                    onTicketUpdate(selectedTicket?.copy(quantity = currentQuantity - 1))
                } else {
                    onTicketUpdate(null)
                }
            }
            plusButton.setOnClickListener {
                onTicketUpdate(selectedTicket?.copy(quantity = currentQuantity + 1) ?: TicketItem(destination, 1))
            }

            return view
        }

        private fun onTicketUpdate(newTicket: TicketItem?) {
            this@DestinationSelectionActivity.selectedTicket = newTicket
            this@DestinationSelectionActivity.updateContinueButtonState()
            this.notifyDataSetChanged()
            appPreferences.saveTicketItem(newTicket)
        }
    }
}