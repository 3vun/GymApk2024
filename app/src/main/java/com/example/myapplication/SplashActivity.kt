package com.example.myapplication

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kreiraj ImageView za prikaz animacije
        val imageView = ImageView(this).apply {
            layoutParams = android.widget.FrameLayout.LayoutParams(
                1000, // Širina u pikselima
                800, // Visina u pikselima
                android.view.Gravity.CENTER // Pozicija u centru
            )
            setBackgroundColor(android.graphics.Color.BLACK) // Crna pozadina
        }

        // Postavi ImageView kao sadržaj
        setContentView(imageView)

        // Kreiraj prvu animaciju
        val animationDrawable1 = AnimationDrawable().apply {
            for (i in 102 downTo 1) { // Dodaj slike unazad, od 102 do 1
                val frameId = resources.getIdentifier("frame_$i", "drawable", packageName)
                val duration = if (i in 83 downTo 79) {
                    75 // Sporiji tempo (75ms)
                } else {
                    25 // Normalni tempo (25ms)
                }
                addFrame(resources.getDrawable(frameId, null), duration)
            }
            isOneShot = true // Animacija se neće ponavljati
        }

        // Kreiraj drugu animaciju
        val animationDrawable2 = AnimationDrawable().apply {
            for (i in 0..32) { // Petlja sada ide od 0 do 31
                val frameId = resources.getIdentifier("framez_$i", "drawable", packageName)
                if (frameId != 0) { // Proveri da li je resurs pronađen
                    addFrame(resources.getDrawable(frameId, null), 25)
                } else {
                    Log.e("AnimationDrawable", "Resource not found for framez_$i")
                }
            }
            isOneShot = true
        }

        // Postavi prvu animaciju na ImageView
        imageView.setImageDrawable(animationDrawable1)
//
//        // Pusti zvuk
//        val mediaPlayer = MediaPlayer.create(this, R.raw.spray)
//        mediaPlayer.start()

        // Pokreni prvu animaciju
        imageView.post {
            animationDrawable1.start()

            // Izračunaj trajanje prve animacije
            val firstAnimationDuration = animationDrawable1.numberOfFrames * 25L

            // Nakon završetka prve animacije, pokreni drugu
            imageView.postDelayed({
                imageView.setImageDrawable(animationDrawable2)
                animationDrawable2.start()

                // Izračunaj trajanje druge animacije
                val secondAnimationDuration = animationDrawable2.numberOfFrames * 25L

                // Nakon završetka druge animacije, prebaci na MainActivity
                imageView.postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Zatvori SplashActivity
                }, secondAnimationDuration)
            }, firstAnimationDuration)
        }
    }
}
