package com.example.proyektmii.data.local

object DummySaldoManager {
    private val saldoMap = mutableMapOf<String, Long>()
    private const val INITIAL_BALANCE = 100_000L

    fun getOrCreateBalance(cardId: String): Long {
        // Hanya akan membuat saldo awal jika ID kartu belum ada
        return saldoMap.getOrPut(cardId) { INITIAL_BALANCE }
    }

    fun updateBalance(cardId: String, amountToDeduct: Long) {
        val currentBalance = getOrCreateBalance(cardId)
        val newBalance = (currentBalance - amountToDeduct).coerceAtLeast(0L)
        saldoMap[cardId] = newBalance
    }

    fun getBalance(cardId: String): Long {
        return saldoMap[cardId] ?: 0L
    }
}