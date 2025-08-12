package com.example.proyektmii.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.proyektmii.data.CartItem
import com.example.proyektmii.data.PaymentHistoryItem
import com.example.proyektmii.data.TicketItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("tmii_app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_PARKING_ENTRY_TIME = "parking_entry_time"
        private const val KEY_PINTU_MASUK_ENTRY_TIME = "pintu_masuk_entry_time"
        private const val KEY_TICKET_ITEM_JSON = "ticket_item_json"
        private const val KEY_CART_ITEMS_JSON = "cart_items_json"

        // New keys for user and payment history
        private const val KEY_USER_CARD_ID = "user_card_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PAYMENT_HISTORY = "payment_history"
    }

    // New functions to save and get user data
    fun saveUserData(cardId: String, name: String) {
        preferences.edit()
            .putString(KEY_USER_CARD_ID, cardId)
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getUserCardId(): String? {
        return preferences.getString(KEY_USER_CARD_ID, null)
    }

    fun getUserName(): String? {
        return preferences.getString(KEY_USER_NAME, null)
    }

    // Fungsi untuk menambahkan riwayat pembayaran ke daftar
    fun addPaymentToHistory(item: PaymentHistoryItem) {
        val history = getPaymentHistory().toMutableList()
        history.add(0, item) // Tambahkan ke awal list untuk urutan terbaru
        val json = gson.toJson(history)
        preferences.edit().putString(KEY_PAYMENT_HISTORY, json).apply()
    }

    // Fungsi untuk mendapatkan semua riwayat pembayaran
    fun getPaymentHistory(): List<PaymentHistoryItem> {
        val json = preferences.getString(KEY_PAYMENT_HISTORY, null)
        val type = object : TypeToken<List<PaymentHistoryItem>>() {}.type
        return if (json != null) {
            try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun clearPaymentHistory() {
        preferences.edit().remove(KEY_PAYMENT_HISTORY).apply()
    }

    // Parking functions
    fun saveParkingEntryTime(timestamp: Long) {
        preferences.edit().putLong(KEY_PARKING_ENTRY_TIME, timestamp).apply()
    }

    fun getParkingEntryTime(): Long {
        return preferences.getLong(KEY_PARKING_ENTRY_TIME, 0L)
    }

    fun clearParkingData() {
        preferences.edit().remove(KEY_PARKING_ENTRY_TIME).apply()
    }

    // Pintu Masuk functions
    fun savePintuMasukEntry(timestamp: Long) {
        preferences.edit().putLong(KEY_PINTU_MASUK_ENTRY_TIME, timestamp).apply()
    }

    fun getPintuMasukEntryTime(): Long {
        return preferences.getLong(KEY_PINTU_MASUK_ENTRY_TIME, 0L)
    }

    fun clearPintuMasukData() {
        preferences.edit().remove(KEY_PINTU_MASUK_ENTRY_TIME).apply()
    }

    // Ticket functions
    fun saveTicketItem(ticketItem: TicketItem?) {
        val editor = preferences.edit()
        if (ticketItem == null) {
            editor.remove(KEY_TICKET_ITEM_JSON)
        } else {
            val json = gson.toJson(ticketItem)
            editor.putString(KEY_TICKET_ITEM_JSON, json)
        }
        editor.apply()
    }

    fun getTicketItem(): TicketItem? {
        val json = preferences.getString(KEY_TICKET_ITEM_JSON, null)
        return if (json != null) {
            try {
                gson.fromJson(json, TicketItem::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Cart functions
    fun saveCartItems(cartItems: List<CartItem>) {
        val json = gson.toJson(cartItems)
        preferences.edit().putString(KEY_CART_ITEMS_JSON, json).apply()
    }

    fun getCartItems(): List<CartItem> {
        val json = preferences.getString(KEY_CART_ITEMS_JSON, null)
        val type = object : TypeToken<List<CartItem>>() {}.type
        return if (json != null) {
            try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun clearCartItems() {
        preferences.edit().remove(KEY_CART_ITEMS_JSON).apply()
    }

    fun clearAllData() {
        preferences.edit().clear().apply()
    }
}