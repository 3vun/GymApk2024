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
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.round


class ArmsTrainingActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var mutableVezbe = mutableStateListOf<Vezba>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("Arms_deleted_exercises", Context.MODE_PRIVATE)

        val ArmsdeletedIds = getArmsDeletedExercises()

        val Armsvezbe = loadVezbe()?.filter {
            !it.Radjeno && it.Pol != "Ž" &&
                    (it.MišićnaParticijaId == 1 || it.MišićnaParticijaId == 2 || it.MišićnaParticijaId == 3) &&
                    it.Id !in ArmsdeletedIds
        }?.sortedBy { it.PrioritetId }?.toMutableList() ?: emptyList()

        // Initialize mutableVezbe state with the filtered exercises
        mutableVezbe.addAll(Armsvezbe)

        setContent {
            ArmsVezbaList(
                vezbe = mutableVezbe,
                onDelete = { ArmsDeletedVezbaId ->
                    saveArmsDeletedExercise(ArmsDeletedVezbaId)
                    mutableVezbe.removeAll { it.Id == ArmsDeletedVezbaId }
                },
                onRestore = { restoreType ->
                    when (restoreType) {
                        "last" -> {
                            restoreLastArmsDeletedExercise()
                        }
                        "all" -> {
                            restoreAllArmsDeletedExercises()
                        }
                    }
                }
            )
        }
    }

    private fun restoreAllArmsDeletedExercises() {
        clearDeletedArmsExercises() // Optionally clear out the deleted IDs
        val refreshedVezbe = loadVezbe()?.filter {
            !it.Radjeno && it.Pol != "Ž" &&
                    (it.MišićnaParticijaId == 1 || it.MišićnaParticijaId == 2 || it.MišićnaParticijaId == 3)
        }?.sortedBy { it.PrioritetId }?.toMutableList() ?: emptyList()
        mutableVezbe.clear()
        mutableVezbe.addAll(refreshedVezbe)
    }

    private fun restoreLastArmsDeletedExercise() {
        val lastDeletedId = sharedPreferences.getInt("last_Arms_deleted_id", -1)
        if (lastDeletedId != -1) {
            val restoredExercise = loadVezbe()?.find { it.Id == lastDeletedId }
            if (restoredExercise != null) {

                // Add the restored exercise to the list
                mutableVezbe.add(restoredExercise)

                // Remove the last exercise from the list
                if (mutableVezbe.isNotEmpty()) {
                    mutableVezbe.removeAt(mutableVezbe.size - 1)
                }

                // Optionally, reapply the filter and sort if necessary
                val deletedIds = getArmsDeletedExercises().toMutableSet()
                deletedIds.remove(lastDeletedId)
                sharedPreferences.edit().putStringSet("Arms_deleted_ids", deletedIds.map { it.toString() }.toSet()).apply()
                sharedPreferences.edit().remove("last_Arms_deleted_id").apply()

                // Reload the entire list and apply the filter to remove deleted exercises
                val refreshedVezbe = loadVezbe()?.filter {
                    !it.Radjeno && it.Pol != "Ž" &&
                            (it.MišićnaParticijaId == 6) &&
                            it.Id !in deletedIds // Make sure deleted exercises are excluded
                }?.sortedBy { it.PrioritetId }?.toMutableList() ?: emptyList()

                // Clear and update the list with the new exercises
                mutableVezbe.clear()
                mutableVezbe.addAll(refreshedVezbe)
            } else {
                Log.d("RestoreExercise", "No exercise found with ID: $lastDeletedId")
            }
        } else {
            Log.d("RestoreExercise", "No last deleted ID found")
        }
    }


    private fun getArmsDeletedExercises(): Set<Int> {
        return sharedPreferences.getStringSet("Arms_deleted_ids", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }

    private fun saveArmsDeletedExercise(id: Int) {
        val deletedIds = getArmsDeletedExercises().toMutableSet()
        deletedIds.add(id)
        sharedPreferences.edit().putStringSet("Arms_deleted_ids", deletedIds.map { it.toString() }.toSet()).apply()
        sharedPreferences.edit().putInt("last_Arms_deleted_id", id).apply()
    }


    private fun clearDeletedArmsExercises() {
        sharedPreferences.edit().putStringSet("Arms_deleted_ids", emptySet()).apply()
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
fun ArmsVezbaDetail(vezba: Vezba, currentIndex: Int, totalCount: Int) {
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
            color = when (vezba.PrioritetId) {
                1 -> Color.Red // Crveni tekst ako je PrioritetId 1
                3 -> Color.Blue // Plavi tekst ako je PrioritetId 3
                else -> Color.Gray // Normalna boja za ostale vrednosti
            },
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "---------------------------------",
            color = Color.Gray,
            fontSize = 15.sp
        )
        val maxTezinaVezbe = 10
        Row {
            // Prikaz zlatnih zvezdica
            repeat(vezba.TezinaVezbe) {
                Image(
                    painter = painterResource(id = R.drawable.star_gold), // Zlatna zvezdica
                    contentDescription = "Zlatna zvezdica",
                    modifier = Modifier.size(15.dp) // Podesite veličinu zvezdica
                )
            }

            // Prikaz sivih zvezdica
            repeat(maxTezinaVezbe - vezba.TezinaVezbe) {
                Image(
                    painter = painterResource(id = R.drawable.star_gray), // Siva zvezdica
                    contentDescription = "Siva zvezdica",
                    modifier = Modifier.size(15.dp) // Podesite veličinu zvezdica
                )
            }
        }
        Text(
            text = "---------------------------------",
            color = Color.Gray,
            fontSize = 15.sp
        )
        Text(
            text = vezba.Mišić.joinToString(" | "),
            color = Color.Gray,
            fontSize = 15.sp
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
            ArmsVideoPlayer(clipPath = vezba.KlipVezbe)
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
fun ArmsVideoPlayer(clipPath: String) {
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
fun ArmsVezbaList(
    vezbe: List<Vezba>,
    onDelete: (Int) -> Unit,
    onRestore: (String) -> Unit // Accept a string to determine restore type
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    // Stopwatch state
    var isStopwatchRunning by remember { mutableStateOf(false) }
    var restTime by remember { mutableStateOf(30) } // Default rest time is 30 seconds
    val minTime: Double = restTime / 60.0
    val roundedMinTime = (round(minTime * 10) / 10)
    var timerValue by remember { mutableStateOf(0) } // Stopwatch countdown
    var showRestDialog by remember { mutableStateOf(false) }

    // Countdown Timer logic
    LaunchedEffect(isStopwatchRunning) {
        if (isStopwatchRunning) {
            while (timerValue > 0) {
                delay(1000)
                timerValue -= 1
            }
            isStopwatchRunning = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // LazyColumn containing cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f), // Ensure cards are in the background
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(vezbe) { vezba ->
                val cardColor = Color.DarkGray
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = cardColor
                    )
                ) {
                    Box {
                        ArmsVezbaDetail(
                            vezba = vezba,
                            currentIndex = vezbe.indexOf(vezba),
                            totalCount = vezbe.size
                        )

                        // Add "X" button (right side)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.delete_button), // Zamenite "ic_close" imenom vaše slike
                                contentDescription = "Delete Button",
                                modifier = Modifier
                                    .clickable {
                                        onDelete(vezba.Id) // Obaveštava roditelja o brisanju
                                    }
                                    .padding(4.dp) // Opcionalno podešavanje margine unutar Box-a
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Row for Bottom Buttons (Info, Stopwatch, Options)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Info button with image
            Button(
                onClick = { showInfoDialog = true },
                modifier = Modifier.size(52.dp), // Match the image size
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp) // Remove default padding
            ) {
                Image(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = "Info",
                    modifier = Modifier.fillMaxSize() // Fill the button size
                )
            }

            // Stopwatch button in the center
            Button(
                onClick = { showRestDialog = true },
                modifier = Modifier.size(55.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.stopwatch),
                    contentDescription = "Stopwatch",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Options button at the bottom right
            Button(
                onClick = { showOptions = true },
                modifier = Modifier.size(52.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Options",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Info dialog
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("Kako raditi ruke:") },
                text = {
                    Column {
                        Text(
                            "Ukupno radiš 6 vežbi, 3 serije od 8-12 ponavljanja.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Add spacing between paragraphs
                        Text(
                            "CRVENE VEŽBE - radiš samo JEDNU i to obavezno PRVU.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Red) // Change the color to red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "SIVE VEŽBE - radiš koliko hoćeš.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Gledaj da uvek radiš druge vežbe prilikom treninga. Posle svakog treninga možeš ukloniti vežbu na x i u svakom trenutnu je možeš i vratiti. Kada nakon izvesnog vremena ukloniš sve vežbe ovim principom možeš vratiti sve vežbe odjednom u settings sa desne strane i tako nastaviti rutinu.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp)) // Add spacing before the icons and text

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between the two columns
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between each image-text pair
                            ) {
                                Text(
                                    "RAMENA + TRICEPS + BICEPS",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // Replace with your image resources
                                Row(
                                    verticalAlignment = Alignment.CenterVertically, // Align the image and text vertically
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between the image and the text
                                ) {
                                    Image(painter = painterResource(id = R.drawable.shoulders_icon), contentDescription = "Icon 1", modifier = Modifier.size(74.dp))
                                    Text("2 vežbe 3x8-12 ponavljanja", style = MaterialTheme.typography.bodyMedium)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(painter = painterResource(id = R.drawable.triceps_icon), contentDescription = "Icon 2", modifier = Modifier.size(74.dp))
                                    Text("2 vežbe 3x8-12 ponavljanja", style = MaterialTheme.typography.bodyMedium)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(painter = painterResource(id = R.drawable.biceps_icon), contentDescription = "Icon 3", modifier = Modifier.size(74.dp))
                                    Text("2 vežbe 3x8-12 ponavljanja", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                    }


                },confirmButton = {
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

        // Rest time dialog
        if (showRestDialog) {
            AlertDialog(
                onDismissRequest = { showRestDialog = false },
                title = { Text("Koliko želiš da odmaraš?") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Pauza: $restTime sekundi")
                        Text("$roundedMinTime minuta")
                        Slider(
                            value = restTime.toFloat(),
                            onValueChange = { restTime = (it / 10).toInt() * 10 }, // Zaokruživanje na najbliži 10
                            valueRange = 10f..300f,
                            steps = 30 // koraka
                        )
                    }
                },
                confirmButton = {
                    Text(
                        text = "Start",
                        modifier = Modifier
                            .clickable {
                                timerValue = restTime
                                isStopwatchRunning = true
                                showRestDialog = false
                            }
                            .padding(8.dp),
                        color = Color.Blue
                    )
                }
            )
        }

        // Show timer countdown
        if (isStopwatchRunning) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(32.dp)
                    .background(Color.Black.copy(alpha = 0.6f)) // 60% transparent black background
                    .padding(16.dp) // Padding for inner content (the timer text)
            ) {
                Text(
                    text = "Vreme: $timerValue",
                    fontSize = 40.sp,
                    color = Color.White
                )
            }
        }

        // Options dialog
        if (showOptions) {
            AlertDialog(
                onDismissRequest = { showOptions = false },
                title = { Text("Opcije:") },
                text = {
                    Column {
                        Button(
                            onClick = {
                                onRestore("last") // Restore last deleted exercise
                                showOptions = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text("Undo Last Deleted")
                        }
                        Button(
                            onClick = {
                                onRestore("all") // Restore all deleted exercises
                                showOptions = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text("Restore All Deleted")
                        }
                    }
                },
                confirmButton = {
                    Text(
                        text = "OK",
                        modifier = Modifier
                            .clickable { showOptions = false }
                            .padding(8.dp),
                        color = Color.Blue
                    )
                }
            )
        }
    }
}



