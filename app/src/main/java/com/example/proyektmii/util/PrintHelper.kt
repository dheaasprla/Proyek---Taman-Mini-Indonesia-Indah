package com.example.proyektmii.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.printer.Format
import com.cloudpos.printer.PrinterDevice
import com.example.proyektmii.R
import java.text.SimpleDateFormat
import java.util.*

object PrintHelper {

    // ---- Data class umum untuk item keranjang ----
    data class ReceiptItem(
        val name: String,
        val qty: Int,
        val price: Long
    )

    // === STRUK UMUM / TIKET MASUK ===
    fun printReceipt(
        context: Context,
        title: String,
        uid: String,
        items: List<ReceiptItem>
    ) {
        Thread {
            var printer: PrinterDevice? = null
            try {
                printer = POSTerminal.getInstance(context)
                    .getDevice("cloudpos.device.printer") as PrinterDevice
                printer.open(0)

                if (printer.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) return@Thread

                val centerLarge = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE)
                }
                val leftNormal = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_LEFT)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM)
                }
                val centerSmall = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_SMALL)
                }

                // Logo TMII (perkecil sebelum print)
                val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.tmii_logo)
                val targetWidth = 200
                val targetHeight = (originalLogo.height * targetWidth) / originalLogo.width
                val scaledLogo = Bitmap.createScaledBitmap(originalLogo, targetWidth, targetHeight, true)
                printer.printBitmap(centerLarge, scaledLogo)

                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(centerLarge, title)

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                printer.printlnText(leftNormal, "UID      : $uid")
                printer.printlnText(leftNormal, "Tanggal  : ${sdf.format(Date())}")

                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftNormal, "Rincian:")

                var total = 0L
                for (item in items) {
                    val subtotal = item.qty * item.price
                    total += subtotal
                    printer.printlnText(
                        leftNormal,
                        "${item.name} x${item.qty}  Rp${item.price} = Rp$subtotal"
                    )
                }

                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftNormal, "TOTAL: Rp$total")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(centerSmall, "Terima kasih")
                printer.printlnText(centerSmall, "Selamat berkunjung ke TMII")
                printer.cutPaper()

            } catch (e: DeviceException) {
                Log.e("PrintHelper", "Print error: ${e.message}", e)
            } finally { try { printer?.close() } catch (_: Exception) {} }
        }.start()
    }

    // === STRUK KELUAR PARKIR ===
    fun printParkingReceipt(
        context: Context,
        uid: String,
        entryTime: String,
        exitTime: String,
        cost: Int,
        balance: Long
    ) {
        Thread {
            var printer: PrinterDevice? = null
            try {
                printer = POSTerminal.getInstance(context)
                    .getDevice("cloudpos.device.printer") as PrinterDevice
                printer.open(0)

                if (printer.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) return@Thread

                val centerLarge = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE)
                }
                val leftMedium = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_LEFT)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM)
                }
                val centerSmall = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_SMALL)
                }

                val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.tmii_logo)
                val targetWidth = 200
                val targetHeight = (originalLogo.height * targetWidth) / originalLogo.width
                val scaledLogo = Bitmap.createScaledBitmap(originalLogo, targetWidth, targetHeight, true)
                printer.printBitmap(centerLarge, scaledLogo)

                printer.printlnText(centerLarge, "----------------------------")
                printer.printlnText(leftMedium, "UID       : $uid")
                printer.printlnText(leftMedium, "Masuk     : $entryTime")
                printer.printlnText(leftMedium, "Keluar    : $exitTime")
                printer.printlnText(leftMedium, "Biaya     : Rp${String.format("%,d", cost)}")
                printer.printlnText(leftMedium, "Sisa Saldo: Rp${String.format("%,d", balance)}")
                printer.printlnText(centerLarge, "----------------------------")
                printer.printlnText(centerSmall, "Terima kasih!")
                printer.cutPaper()

            } catch (e: DeviceException) {
                Log.e("PrintHelper", "Print parkir error: ${e.message}", e)
            } finally { try { printer?.close() } catch (_: Exception) {} }
        }.start()
    }

    // === STRUK PEMBAYARAN KANTIN ===
    fun printCanteenReceipt(
        context: Context,
        uid: String,
        items: List<ReceiptItem>,
        totalPrice: Long,
        balance: Long
    ) {
        Thread {
            var printer: PrinterDevice? = null
            try {
                printer = POSTerminal.getInstance(context)
                    .getDevice("cloudpos.device.printer") as PrinterDevice
                printer.open(0)

                val centerLarge = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE)
                }
                val leftMedium = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_LEFT)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM)
                }
                val centerSmall = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_SMALL)
                }

                val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.tmii_logo)
                val targetWidth = 200
                val targetHeight = (originalLogo.height * targetWidth) / originalLogo.width
                val scaledLogo = Bitmap.createScaledBitmap(originalLogo, targetWidth, targetHeight, true)
                printer.printBitmap(centerLarge, scaledLogo)

                printer.printlnText(centerLarge, "KANTIN TMII")
                printer.printlnText(centerLarge, "------------------------------")

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val dateStr = sdf.format(Date())
                printer.printlnText(leftMedium, "UID : $uid")
                printer.printlnText(leftMedium, "Tanggal : $dateStr")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftMedium, "Pesanan:")

                for (item in items) {
                    val subtotal = item.price * item.qty
                    printer.printlnText(leftMedium, "${item.name} x${item.qty}  Rp$subtotal")
                }

                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftMedium, "Total Bayar : Rp$totalPrice")
                printer.printlnText(leftMedium, "Sisa Saldo  : Rp$balance")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(centerSmall, "Terima kasih! Selamat Menikmati")
                printer.cutPaper()

            } catch (e: DeviceException) {
                Log.e("PrintHelper", "Print kantin error: ${e.message}", e)
            } finally { try { printer?.close() } catch (_: Exception) {} }
        }.start()
    }

    // === STRUK TIKET DESTINASI ===
    fun printDestinationReceipt(
        context: Context,
        uid: String,
        destinationName: String,
        qty: Int,
        price: Long,
        total: Long,
        balance: Long
    ) {
        Thread {
            var printer: PrinterDevice? = null
            try {
                printer = POSTerminal.getInstance(context)
                    .getDevice("cloudpos.device.printer") as PrinterDevice
                printer.open(0)

                val centerLarge = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE)
                }
                val leftMedium = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_LEFT)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM)
                }
                val centerSmall = Format().apply {
                    setParameter(Format.FORMAT_ALIGN, Format.FORMAT_ALIGN_CENTER)
                    setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_SMALL)
                }

                val originalLogo = BitmapFactory.decodeResource(context.resources, R.drawable.tmii_logo)
                val targetWidth = 200
                val targetHeight = (originalLogo.height * targetWidth) / originalLogo.width
                val scaledLogo = Bitmap.createScaledBitmap(originalLogo, targetWidth, targetHeight, true)
                printer.printBitmap(centerLarge, scaledLogo)

                printer.printlnText(centerLarge, "TIKET DESTINASI TMII")
                printer.printlnText(centerLarge, "------------------------------")

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val dateStr = sdf.format(Date())
                printer.printlnText(leftMedium, "UID : $uid")
                printer.printlnText(leftMedium, "Tanggal : $dateStr")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftMedium, "$destinationName x$qty  Rp${price * qty}")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(leftMedium, "TOTAL : Rp$total")
                printer.printlnText(leftMedium, "Sisa Saldo : Rp$balance")
                printer.printlnText(centerLarge, "------------------------------")
                printer.printlnText(centerSmall, "Selamat menikmati wahana!")
                printer.cutPaper()

            } catch (e: DeviceException) {
                Log.e("PrintHelper", "Print destinasi error: ${e.message}", e)
            } finally { try { printer?.close() } catch (_: Exception) {} }
        }.start()
    }
}
