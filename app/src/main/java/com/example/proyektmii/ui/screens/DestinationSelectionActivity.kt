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
import com.example.proyektmii.data.Destination
import com.example.proyektmii.data.TicketItem

class DestinationSelectionActivity : AppCompatActivity() {

    private var selectedTicket: TicketItem? = null
    private lateinit var adapter: DestinationAdapter
    private lateinit var btnProceed: Button
    private lateinit var rv: RecyclerView
    private var isWahana = false
    private lateinit var destinations: List<Destination> // Simpan daftar destinations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_selection)

        isWahana = intent.getBooleanExtra("isWahana", false)
        val tvTitle = findViewById<TextView>(R.id.tvSelTitle)
        tvTitle.text = if (isWahana) "Wahana & Rekreasi" else "Museum"

        val btnBack = findViewById<ImageButton>(R.id.btnBackSel)
        btnBack.setOnClickListener { finish() }

        btnProceed = findViewById(R.id.btnProceed)
        btnProceed.isEnabled = false

        rv = findViewById(R.id.rvDestinations)
        destinations = loadDestinations(isWahana) // Simpan ke variabel class
        setupAdapter(destinations)
    }

    private fun loadDestinations(isWahana: Boolean): List<Destination> {
        return if (isWahana) {
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
    }

    private fun setupAdapter(destinations: List<Destination>) {
        rv.layoutManager = GridLayoutManager(this, 2)
        adapter = DestinationAdapter(
            items = destinations,
            selectedName = selectedTicket?.destination?.name,
            selectedQty = selectedTicket?.quantity ?: 0,
            onSelect = { dest ->
                if (selectedTicket?.destination?.name != dest.name) {
                    selectedTicket = TicketItem(dest, 1)
                }
                refreshAdapter()
            },
            onIncrease = { dest ->
                if (selectedTicket?.destination?.name == dest.name) {
                    selectedTicket = selectedTicket?.copy(quantity = (selectedTicket?.quantity ?: 0) + 1)
                } else {
                    selectedTicket = TicketItem(dest, 1)
                }
                refreshAdapter()
            },
            onDecrease = { dest ->
                if (selectedTicket?.destination?.name == dest.name) {
                    val newQty = (selectedTicket?.quantity ?: 1) - 1
                    selectedTicket = if (newQty > 0) selectedTicket?.copy(quantity = newQty) else null
                }
                refreshAdapter()
            }
        )
        rv.adapter = adapter

        btnProceed.setOnClickListener {
            selectedTicket?.let { ticket ->
                val intent = Intent(this, TicketCartActivity::class.java)
                intent.putExtra("ticket", ticket)
                startActivity(intent)
            }
        }
    }

    private fun refreshAdapter() {
        // Gunakan variabel destinations yang sudah disimpan
        rv.adapter = DestinationAdapter(
            items = destinations,
            selectedName = selectedTicket?.destination?.name,
            selectedQty = selectedTicket?.quantity ?: 0,
            onSelect = { dest ->
                if (selectedTicket?.destination?.name != dest.name) selectedTicket = TicketItem(dest, 1)
                refreshAdapter()
            },
            onIncrease = { dest ->
                if (selectedTicket?.destination?.name == dest.name) {
                    selectedTicket = selectedTicket?.copy(quantity = (selectedTicket?.quantity ?: 0) + 1)
                } else selectedTicket = TicketItem(dest, 1)
                refreshAdapter()
            },
            onDecrease = { dest ->
                if (selectedTicket?.destination?.name == dest.name) {
                    val newQty = (selectedTicket?.quantity ?: 1) - 1
                    selectedTicket = if (newQty > 0) selectedTicket?.copy(quantity = newQty) else null
                }
                refreshAdapter()
            }
        )
        btnProceed.isEnabled = selectedTicket != null
    }
}