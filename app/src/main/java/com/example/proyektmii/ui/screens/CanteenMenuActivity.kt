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
import androidx.cardview.widget.CardView
import com.example.proyektmii.R
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.MenuItem
import com.example.proyektmii.data.local.AppPreferences
import java.text.NumberFormat
import java.util.Locale

class CanteenMenuActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var menuGrid: GridView
    private lateinit var cartSummaryCard: CardView
    private lateinit var totalSummaryText: TextView
    private lateinit var payButton: Button
    private lateinit var backButton: ImageButton

    private var cartItems = listOf<CartItem>()

    private val menuItems = listOf(
        MenuItem("Nasi Goreng", 20000, R.drawable.nasgor),
        MenuItem("Mie Ayam", 15000, R.drawable.mieayam),
        MenuItem("Es Jeruk", 8000, R.drawable.esjeruk),
        MenuItem("Teh manis", 5000, R.drawable.esteh)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canteen_menu)

        appPreferences = AppPreferences(this)

        try {
            menuGrid = findViewById(R.id.menu_grid)
            cartSummaryCard = findViewById(R.id.cart_summary_card)
            totalSummaryText = findViewById(R.id.total_summary_text)
            payButton = findViewById(R.id.pay_button)
            backButton = findViewById(R.id.back_button_canteen)

            backButton.setOnClickListener {
                onBackPressed()
            }

            payButton.setOnClickListener {
                val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("totalPrice", totalPrice)
                startActivity(intent)
            }

            loadCartItems()
        } catch (e: Exception) {
            e.printStackTrace()
            // Tampilkan pesan kesalahan jika ada masalah
            // Ini bisa membantu Anda mengetahui letak masalahnya
            // di logcat saat crash terjadi.
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartItems()
    }

    private fun loadCartItems() {
        cartItems = appPreferences.getCartItems()
        val adapter = MenuItemGridAdapter(menuItems)
        menuGrid.adapter = adapter
        updateCartSummary()
    }

    private fun updateCartSummary() {
        val totalItems = cartItems.sumOf { it.quantity }
        val totalPrice = cartItems.sumOf { it.menuItem.price * it.quantity }
        if (totalItems > 0) {
            cartSummaryCard.visibility = View.VISIBLE
            totalSummaryText.text = "(${totalItems} item) Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(totalPrice)}"
        } else {
            cartSummaryCard.visibility = View.GONE
        }
    }

    inner class MenuItemGridAdapter(private val menuItems: List<MenuItem>) : BaseAdapter() {
        override fun getCount(): Int = menuItems.size
        override fun getItem(position: Int): Any = menuItems[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(parent?.context)
                .inflate(R.layout.grid_item_menu, parent, false)

            val menuItem = menuItems[position]

            val imageView: ImageView = view.findViewById(R.id.menu_item_image)
            val nameTextView: TextView = view.findViewById(R.id.menu_item_name)
            val priceTextView: TextView = view.findViewById(R.id.menu_item_price)
            val addButton: Button = view.findViewById(R.id.add_button)
            val quantityLayout: LinearLayout = view.findViewById(R.id.quantity_control)
            val minusButton: Button = view.findViewById(R.id.minus_button)
            val plusButton: Button = view.findViewById(R.id.plus_button)
            val quantityTextView: TextView = view.findViewById(R.id.quantity_text)

            imageView.setImageResource(menuItem.imageRes ?: R.drawable.nasgor)
            nameTextView.text = menuItem.name
            priceTextView.text = "Rp ${NumberFormat.getNumberInstance(Locale("in", "ID")).format(menuItem.price)}"

            val existingCartItem = cartItems.find { it.menuItem.name == menuItem.name }
            val currentQuantity = existingCartItem?.quantity ?: 0

            if (currentQuantity > 0) {
                addButton.visibility = View.GONE
                quantityLayout.visibility = View.VISIBLE
                quantityTextView.text = currentQuantity.toString()
            } else {
                addButton.visibility = View.VISIBLE
                quantityLayout.visibility = View.GONE
            }

            addButton.setOnClickListener {
                val updatedCart = if (existingCartItem != null) {
                    cartItems.map { if (it.menuItem.name == menuItem.name) it.copy(quantity = it.quantity + 1) else it }
                } else {
                    cartItems + CartItem(menuItem, 1)
                }
                cartItems = updatedCart
                appPreferences.saveCartItems(cartItems)
                updateCartSummary()
                notifyDataSetChanged()
            }
            minusButton.setOnClickListener {
                if (currentQuantity > 1) {
                    val updatedCart = cartItems.map { if (it.menuItem.name == menuItem.name) it.copy(quantity = it.quantity - 1) else it }
                    cartItems = updatedCart
                } else {
                    cartItems = cartItems.filter { it.menuItem.name != menuItem.name }
                }
                appPreferences.saveCartItems(cartItems)
                updateCartSummary()
                notifyDataSetChanged()
            }
            plusButton.setOnClickListener {
                val updatedCart = cartItems.map { if (it.menuItem.name == menuItem.name) it.copy(quantity = it.quantity + 1) else it }
                cartItems = updatedCart
                appPreferences.saveCartItems(cartItems)
                updateCartSummary()
                notifyDataSetChanged()
            }

            return view
        }
    }
}