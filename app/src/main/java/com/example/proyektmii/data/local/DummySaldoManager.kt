package com.example.proyektmii.data.local

object DummySaldoManager {
    private val saldoMap = mutableMapOf<String, Long>()

    fun getOrCreateBalance(cardId: String): Long {
        return saldoMap.getOrPut(cardId) { 100_000L } // setiap kartu baru langsung ada Rp100.000
    }

    fun updateBalance(cardId: String, amount: Long) {
        val current = saldoMap.getOrPut(cardId) { 100_000L }
        saldoMap[cardId] = (current + amount).coerceAtLeast(0L)
    }

    fun getBalance(cardId: String): Long {
        return saldoMap[cardId] ?: 0L
    }
}
