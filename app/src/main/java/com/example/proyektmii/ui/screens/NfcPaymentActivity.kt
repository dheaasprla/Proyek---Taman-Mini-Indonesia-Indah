package com.example.proyektmii.ui.screens

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.example.proyektmii.R
import java.text.NumberFormat
import java.util.Locale

class NfcPaymentActivity : AppCompatActivity() {
    companion object { private const val TAG = "NfcPaymentActivity" }

    private lateinit var balanceTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var backButton: ImageButton
    private var readerObj: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_payment)

        balanceTextView = findViewById(R.id.total_price_text)
        statusTextView = findViewById(R.id.nfc_instruction_text)
        backButton = findViewById(R.id.back_button_nfc_payment)
        backButton.setOnClickListener { onBackPressed() }

        checkNfcBalance()
    }

    private fun checkNfcBalance() {
        balanceTextView.text = "Rp -"
        statusTextView.text = "Menunggu kartu..."

        Thread {
            try {
                readerObj = POSTerminal.getInstance(applicationContext)
                    .getDevice("cloudpos.device.rfcardreader")
                val deviceClass = "com.cloudpos.rfcardreader.RFCardReaderDevice"
                val mode = getIntConstant(deviceClass, "MODE_AUTO") ?: 0
                val rate = getIntConstant(deviceClass, "RATE_106K") ?: 1
                if (!invokeOpen(readerObj, mode, rate)) {
                    runOnUiThread { statusTextView.text = "Gagal buka reader" }; return@Thread
                }

                val resultObj = invokeWaitForCardPresent(readerObj, 30000)
                val rc = getResultCode(resultObj)
                if (rc != 0) { runOnUiThread { statusTextView.text = "Kartu tidak terdeteksi (kode:$rc)" }; return@Thread }

                val apdu = hexStringToByteArray("FFCA000000")
                val response = ByteArray(256)
                val len = invokeTransmit(readerObj, 0, apdu, response)
                if (len > 0) {
                    val hex = response.copyOfRange(0, len).toHex()
                    val balance = hexStringToDecimal(hex)
                    runOnUiThread {
                        statusTextView.text = "Data terbaca"
                        balanceTextView.text = "Rp " + NumberFormat.getNumberInstance(Locale("in","ID")).format(balance)
                    }
                } else runOnUiThread { statusTextView.text = "Gagal membaca kartu (len=$len)" }
            } catch (e: DeviceException) {
                Log.e(TAG, "DeviceException", e); runOnUiThread { statusTextView.text = "Error device: ${e.message}" }
            } catch (e: Exception) {
                Log.e(TAG, "Exception", e); runOnUiThread { statusTextView.text = "Kesalahan: ${e.message}" }
            } finally {
                safeClose(readerObj)
            }
        }.start()
    }

    // reuse the same helper methods as in CheckBalanceActivity (you can copy-paste or extract to a util file)
    private fun getIntConstant(className: String, fieldName: String): Int? {
        return try { Class.forName(className).getField(fieldName).getInt(null) } catch (e: Exception) { null }
    }
    private fun invokeOpen(device: Any?, mode: Int, speed: Int): Boolean {
        if (device == null) return false
        return try {
            val cls = device.javaClass
            try { cls.getMethod("open", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType).invoke(device, mode, speed); true }
            catch (ex1: NoSuchMethodException) {
                try { cls.getMethod("open", Int::class.javaPrimitiveType).invoke(device, mode); true }
                catch (ex2: NoSuchMethodException) {
                    try { cls.getMethod("open").invoke(device); true } catch (e: Exception) { false }
                }
            }
        } catch (e: Exception) { false }
    }
    private fun invokeWaitForCardPresent(device: Any?, timeoutMs: Long): Any? {
        if (device == null) return null
        val cls = device.javaClass
        try {
            try { return cls.getMethod("waitForCardPresent", Int::class.javaPrimitiveType).invoke(device, timeoutMs.toInt()) } catch (_: NoSuchMethodException) {}
            try { return cls.getMethod("waitForCardPresent", Long::class.javaPrimitiveType).invoke(device, timeoutMs) } catch (_: NoSuchMethodException) {}
            return cls.getMethod("waitForCardPresent").invoke(device)
        } catch (e: Exception) { return null }
    }
    private fun getResultCode(resultObj: Any?): Int {
        if (resultObj == null) return -1
        val cls = resultObj.javaClass
        listOf("getResultCode","getErrorCode","getResult").forEach { name ->
            try {
                val m = cls.getMethod(name)
                val r = m.invoke(resultObj) ?: return@forEach
                return when (r) { is Number -> r.toInt(); is Boolean -> if (r) 1 else 0; else -> r.toString().toIntOrNull() ?: 0 }
            } catch (e: Exception) { /* ignore */ }
        }
        cls.declaredFields.forEach { f ->
            if (f.name.equals("resultCode", true) || f.name.equals("errorCode", true)) {
                try { f.isAccessible = true; val v = f.get(resultObj); if (v is Number) return v.toInt(); return v.toString().toIntOrNull() ?: 0 } catch (_: Exception) {}
            }
        }
        return 0
    }
    private fun invokeTransmit(device: Any?, slot: Int, apdu: ByteArray, response: ByteArray): Int {
        if (device == null) return -1
        val cls = device.javaClass
        val signatures = listOf(
            arrayOf(Int::class.javaPrimitiveType, ByteArray::class.java, ByteArray::class.java),
            arrayOf(ByteArray::class.java, ByteArray::class.java),
            arrayOf(Int::class.javaPrimitiveType, ByteArray::class.java),
            arrayOf(ByteArray::class.java)
        )
        val names = listOf("transmit","transceive","send","sendAPDU","sendApdu")
        for (n in names) {
            for (sig in signatures) {
                try {
                    val m = cls.getMethod(n, *sig)
                    val ret = when (sig.size) {
                        3 -> m.invoke(device, slot, apdu, response)
                        2 -> m.invoke(device, apdu, response)
                        1 -> m.invoke(device, apdu)
                        else -> m.invoke(device)
                    }
                    when (ret) { is Number -> return ret.toInt(); is ByteArray -> return ret.size; is Boolean -> return if (ret) 1 else 0; else -> return ret?.toString()?.toIntOrNull() ?: -1 }
                } catch (_: NoSuchMethodException) { }
                catch (e: Exception) { Log.w(TAG, "invokeTransmit $n failed: ${e.message}") }
            }
        }
        return -1
    }
    private fun safeClose(device: Any?) {
        if (device == null) return
        try { device.javaClass.getMethod("close").invoke(device) } catch (_: Exception) {}
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length; val data = ByteArray(len / 2); var i = 0
        while (i < len) { data[i/2] = ((Character.digit(s[i],16) shl 4) + Character.digit(s[i+1],16)).toByte(); i += 2 }
        return data
    }
    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun hexStringToDecimal(hex: String): Long = hex.toLongOrNull(16) ?: 0L
}
