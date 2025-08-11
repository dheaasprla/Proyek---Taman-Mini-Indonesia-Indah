package com.example.proyektmii.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.TicketItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Kelas helper untuk mengelola semua data yang disimpan di SharedPreferences
class AppPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("tmii_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PARKING_ENTRY_TIME = "parking_entry_time"
        private const val KEY_TICKET_ITEM_JSON = "ticket_item_json"
        private const val KEY_CART_ITEMS_JSON = "cart_items_json"
    }

    // --- Fungsi untuk Parkir ---
    fun saveParkingEntryTime(timestamp: Long) {
        preferences.edit().putLong(KEY_PARKING_ENTRY_TIME, timestamp).apply()
    }

    fun getParkingEntryTime(): Long {
        return preferences.getLong(KEY_PARKING_ENTRY_TIME, 0L)
    }

    fun clearParkingData() {
        preferences.edit().remove(KEY_PARKING_ENTRY_TIME).apply()
    }

    // --- Fungsi untuk Tiket Destinasi ---
    fun saveTicketItem(ticketItem: TicketItem?) {
        val editor = preferences.edit()
        if (ticketItem == null) {
            editor.remove(KEY_TICKET_ITEM_JSON)
        } else {
            val json = Gson().toJson(ticketItem)
            editor.putString(KEY_TICKET_ITEM_JSON, json)
        }
        editor.apply()
    }

    fun getTicketItem(): TicketItem? {
        val json = preferences.getString(KEY_TICKET_ITEM_JSON, null)
        return if (json != null) {
            Gson().fromJson(json, TicketItem::class.java)
        } else {
            null
        }
    }

    // --- Fungsi untuk Kantin ---
    fun saveCartItems(cartItems: List<CartItem>) {
        val json = Gson().toJson(cartItems)
        preferences.edit().putString(KEY_CART_ITEMS_JSON, json).apply()
    }

    fun getCartItems(): List<CartItem> {
        val json = preferences.getString(KEY_CART_ITEMS_JSON, null)
        val type = object : TypeToken<List<CartItem>>() {}.type
        return if (json != null) {
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }
}