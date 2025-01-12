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
import com.example.myapplication.Data.Misic
import com.example.myapplication.Data.MisicResponse
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


class MusclesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusclesMisicList(context = this)
        }
    }
}

@Composable
fun MusclesMisicList(context: Context) {
    val misici = remember {
        loadMisici(context)
    }

    if (misici == null) {
        Text(
            text = "Greška pri učitavanju mišića.",
            color = Color.Red,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(misici) { misic ->
                MisicCard(misic)
            }
        }
    }
}

@Composable
fun MisicCard(misic: Misic) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Dodaj logiku za klik */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Slika mišića
            val imagePainter = rememberAsyncImagePainter(
                model = misic.SlikaMisica ?: R.drawable.main_activity_zene
            )
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Naziv mišića
            Text(
                text = misic.Naziv,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Opis mišića
            Text(
                text = misic.Opis,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

fun loadMisici(context: Context): List<Misic>? {
    return try {
        val inputStream = context.assets.open("misici.json")
        val reader = InputStreamReader(inputStream)
        val response = Gson().fromJson(reader, MisicResponse::class.java)
        response.Misic
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("LoadMisiciError", "Greška pri učitavanju: ${e.message}")
        null
    }
}





