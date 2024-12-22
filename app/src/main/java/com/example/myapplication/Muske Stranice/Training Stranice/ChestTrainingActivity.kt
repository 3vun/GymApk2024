package com.example.myapplication.Muske

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.Data.Vezba
import com.example.myapplication.Data.VezbeResponse
import com.example.myapplication.R
import com.google.gson.Gson
import java.io.InputStreamReader



class ChestTrainingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chestvezbe = loadVezbe()?.filter {
            !it.Radjeno && it.Pol != "Ž" && (it.MišićnaParticijaId == 5 )
        }?.let { allVezbe ->
            val selectedVezbe = allVezbe.sortedBy { it.PrioritetId }.toMutableList()
            selectedVezbe
        } ?: emptyList()

        setContent {
            ChestVezbaList(chestvezbe)
        }


    }

    private fun loadVezbe(): List<Vezba>? {
        return try {
            val inputStream = assets.open("vezbe.json")
            val reader = InputStreamReader(inputStream)
            val response = Gson().fromJson(reader, VezbeResponse::class.java)
            response.Vezbe
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}


@Composable
fun ChestVezbaDetail(vezba: Vezba, currentIndex: Int, totalCount: Int) {
    var timerValue by remember { mutableStateOf(90) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) } // Dodata promenljiva za video
    var isTimerStopped by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(5.dp)
    ) {
        Text(
            text = vezba.Naziv,
            color = Color.Gray,
            fontSize = 25.sp
        )

        val imagePainter = when {
            !vezba.SlikaVezbe.isNullOrEmpty() && vezba.SlikaVezbe.startsWith("http") -> {
                rememberAsyncImagePainter(model = vezba.SlikaVezbe)
            }
            !vezba.SlikaVezbe.isNullOrEmpty() -> {
                val resourceId = try {
                    val resourceName = vezba.SlikaVezbe.substringAfterLast("drawable/")
                    val resId = R.drawable::class.java.getDeclaredField(resourceName).getInt(null)
                    resId
                } catch (e: Exception) {
                    R.drawable.exercises_image
                }
                painterResource(id = resourceId)
            }
            else -> painterResource(id = R.drawable.exercises_image)
        }

        // Klik na sliku pokreće ili zatvara video
        Image(
            painter = imagePainter,
            contentDescription = vezba.Naziv,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 3.dp)
                .clickable {
                    isVideoPlaying = if (isVideoPlaying) {
                        false // Zatvara video
                    } else {
                        true // Pokreće video
                    }
                }
        )

        // Prikaz video plejera ako je video pokrenut
        if (isVideoPlaying && !vezba.KlipVezbe.isNullOrEmpty()) {
            ChestVideoPlayer(clipPath = vezba.KlipVezbe)
        }
        Text(
            text = vezba.InstrukcijeVezbe,
            color = Color.Gray,
            fontSize = 17.sp,
            modifier = Modifier.padding(bottom = 3.dp)
        )
    }
}

@Composable
fun ChestVideoPlayer(clipPath: String) {
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                val resourceId = context.resources.getIdentifier(
                    clipPath.substringAfter("drawable/"),
                    "raw",
                    context.packageName
                )
                if (resourceId != 0) { // Proverava da li je resurs pronađen
                    val videoUri = Uri.parse("android.resource://${context.packageName}/$resourceId")
                    setVideoURI(videoUri)
                    start()

                    // Omogućavanje loop-a
                    setOnCompletionListener {
                        start() // Ponovo pokreće video kada završi
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}


@Composable
fun ChestVezbaList(vezbe: List<Vezba>) {
    var showInfoDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (vezbe.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(vezbe) { vezba ->
                    val cardColor = when (vezba.PrioritetId) {
                        1 -> Color(0x80FF0000) // Providna crvena
                        3 -> Color(0x800000FF) // Providna plava
                        else -> Color.DarkGray
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp), // Razmak između kartica
                        shape = androidx.compose.material3.MaterialTheme.shapes.medium, // Zaobljeni uglovi
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = cardColor
                        )
                    ) {
                        ChestVezbaDetail(vezba = vezba, currentIndex = vezbe.indexOf(vezba), totalCount = vezbe.size)
                    }

                    Spacer(modifier = Modifier.height(10.dp)) // Razmak između vežbi
                }
            }
        } else {
            Text("Nema dostupnih vežbi", color = Color.Red)
        }

        // Oblačić u donjem desnom uglu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                onClick = { showInfoDialog = true },
                modifier = Modifier.size(60.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("i", color = Color.Black, fontSize = 24.sp)
            }
        }

        // Dijalog sa informacijama
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("Zapamti:") },
                text = { Text("Ukupno radiš 5-6 vežbi, 3 serije od 8-12 ponavljanja. CRVENE VEZBE - radiš samo JEDNU i to obavezno PRVU, SIVE VEZBE - radis koliko hoćeš a ŽUTE VEŽBE - radiš samo JEDNU ali POSLEDNJU. Gledaj da uvek radiš druge vežbe prilikom treninga.") },
                confirmButton = {
                    Text(
                        text = "OK",
                        modifier = Modifier
                            .clickable { showInfoDialog = false }
                            .padding(8.dp),
                        color = Color.Blue
                    )
                }
            )
        }
    }
}
