package com.example.proyektmii

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyektmii.ui.theme.ProyekTMIITheme
import com.example.proyektmii.ui.viewmodel.NfcViewModel

class MainActivity : ComponentActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private val viewModel: NfcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisiasi NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        setContent {
            ProyekTMIITheme {
                Surface(Modifier.fillMaxSize()) {
                    EntryFlow { cardId ->
                        // Save & toast
                        viewModel.saveCardId(cardId)
                        Toast.makeText(this, "Card ID: $cardId", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::nfcAdapter.isInitialized && nfcAdapter.isEnabled) {
            nfcAdapter.enableForegroundDispatch(
                this,
                pendingIntent,
                arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)),
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (::nfcAdapter.isInitialized) {
            nfcAdapter.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent
            ?.takeIf { it.action == NfcAdapter.ACTION_TAG_DISCOVERED }
            ?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            ?.let { tag ->
                val cardId = tag.id.joinToString("") { b -> "%02x".format(b) }
                // Panggil callback lewat viewModel LiveData
                viewModel.onCardScanned(cardId)
            }
    }
}

@Composable
fun EntryFlow(onCardDetected: (String) -> Unit) {
    // State untuk swap screen
    var scannedId by remember { mutableStateOf<String?>(null) }
    val vm = remember { onCardDetected } // simpan callback

    // Observasi LiveData (atau Flow) dari ViewModel
    // misal: vm.cards.observeAsState()...

    Crossfade(targetState = scannedId != null) { success ->
        if (!success) {
            // Screen “Tempelkan Kartu Anda”
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFFFE047), Color.White))
                    )
                    .clickable {
                        // untuk simulasi testing, ganti dengan onNewIntent nyata
                        val fake = "1234567890"
                        scannedId = fake
                        vm(fake)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Silakan Tempelkan Kartu Anda",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Screen “Sukses! Silahkan Masuk”
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFD32F2F)), // merah cerah
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lingkaran kuning dengan ceklis
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE047)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✔", fontSize = 48.sp, color = Color(0xFFD32F2F))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "SUKSES!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Silahkan Masuk",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
