package com.example.halamantiket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.halamantiket.ui.theme.HalamanTiketTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HalamanTiketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TicketBookingParentScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TicketBookingParentScreen(modifier: Modifier = Modifier) {
    // 1. State Hoisting: State dikelola oleh Parent
    var hargaTiket by remember { mutableStateOf(25000) } // Harga Tiket (Kelipatan 25.000)
    var jumlahTiket by remember { mutableStateOf(1) }    // Jumlah Tiket
    var namaPembeli by remember { mutableStateOf("") }     // Nama Pembeli Tiket

    // State internal untuk status pesanan dan kontrol aksi pemesanan
    var statusPesanan by remember { mutableStateOf("Silahkan Pesan Tiket") }
    var submitTrigger by remember { mutableStateOf<Long?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // 2. Gunakan LaunchedEffect untuk menangani logika perubahan status asinkronous & validasi
    LaunchedEffect(submitTrigger) {
        if (submitTrigger != null) {
            if (namaPembeli.isBlank()) {
                statusPesanan = "Nama Harus Diisi!"
                isProcessing = false
            } else {
                statusPesanan = "Memproses Pesanan..."
                isProcessing = true
                
                // Menunggu selama 5 detik sesuai instruksi
                delay(5000)
                
                statusPesanan = "Tiket berhasil dipesan!"
                isProcessing = false
            }
        }
    }

    // Memanggil Child Composable dengan mengoper state dan callback
    TicketBookingContent(
        hargaTiket = hargaTiket,
        jumlahTiket = jumlahTiket,
        namaPembeli = namaPembeli,
        statusPesanan = statusPesanan,
        isProcessing = isProcessing,
        onNamaChange = { namaPembeli = it },
        onJumlahChange = { jumlahTiket = it },
        onPesanClick = {
            // Trigger LaunchedEffect
            submitTrigger = System.currentTimeMillis()
        },
        modifier = modifier
    )
}

@Composable
fun TicketBookingContent(
    hargaTiket: Int,
    jumlahTiket: Int,
    namaPembeli: String,
    statusPesanan: String,
    isProcessing: Boolean,
    onNamaChange: (String) -> Unit,
    onJumlahChange: (Int) -> Unit,
    onPesanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hitung total harga berdasarkan jumlah tiket (kelipatan 25k)
    val totalHarga = hargaTiket * jumlahTiket
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }
    val formattedTotal = formatter.format(totalHarga)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1976D2))
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Pemesanan Tiket",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Form Container Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            // Input Nama Pembeli (State 3)
            Text(
                text = "Nama",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            OutlinedTextField(
                value = namaPembeli,
                onValueChange = onNamaChange,
                placeholder = { Text(text = "Masukkan nama Anda", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isProcessing,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF212121)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF212121),
                    unfocusedTextColor = Color(0xFF212121),
                    disabledTextColor = Color(0xFF757575),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    disabledBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Input Jumlah Tiket (State 2)
            Text(
                text = "Jumlah Tiket",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Minus
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            if (jumlahTiket > 1 && !isProcessing) Color(0xFFE3F2FD) else Color(0xFFF5F5F5), 
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = jumlahTiket > 1 && !isProcessing) {
                            onJumlahChange(jumlahTiket - 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (jumlahTiket > 1 && !isProcessing) Color(0xFF1976D2) else Color.LightGray
                    )
                }

                // Angka Jumlah
                Text(
                    text = jumlahTiket.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF212121)
                )

                // Tombol Plus
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(if (!isProcessing) Color(0xFFE3F2FD) else Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .clickable(enabled = !isProcessing) {
                            onJumlahChange(jumlahTiket + 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isProcessing) Color(0xFF1976D2) else Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Informasi Total Bayar yang bertambah setiap kelipatan 25k (jumlah tiket berubah)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Bayar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Text(
                    text = formattedTotal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Pesan Tiket
            Button(
                onClick = onPesanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isProcessing,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFB0BEC5),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Pesan Tiket",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tampilan Status Berdasarkan Kondisi State & LaunchedEffect
            val statusTextColor = when (statusPesanan) {
                "Nama Harus Diisi!" -> Color(0xFFC62828) // Merah tua
                "Memproses Pesanan..." -> Color(0xFF1565C0) // Biru tua
                "Tiket berhasil dipesan!" -> Color(0xFF2E7D32) // Hijau tua
                else -> Color(0xFF616161) // Abu-abu tua
            }

            val finalBgColor = when (statusPesanan) {
                "Nama Harus Diisi!" -> Color(0xFFFFEBEE)
                "Memproses Pesanan..." -> Color(0xFFE3F2FD)
                "Tiket berhasil dipesan!" -> Color(0xFFE8F5E9)
                else -> Color(0xFFF5F5F5)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(finalBgColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                when (statusPesanan) {
                    "Memproses Pesanan..." -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = statusTextColor,
                            strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    }
                    "Tiket berhasil dipesan!" -> {
                        Text(
                            text = "✓",
                            color = statusTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    "Nama Harus Diisi!" -> {
                        Text(
                            text = "⚠",
                            color = statusTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Text(
                    text = "Status: $statusPesanan",
                    color = statusTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketBookingPreview() {
    HalamanTiketTheme {
        TicketBookingParentScreen()
    }
}
