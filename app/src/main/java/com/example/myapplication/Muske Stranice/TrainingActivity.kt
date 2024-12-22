package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Muske.*

class TrainingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Glavni FrameLayout za slojeve
        val rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Pozadinska slika
        val backgroundImage = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setImageResource(R.drawable.main_activity_muskarci) // Postavite resurs pozadinske slike
            scaleType = ImageView.ScaleType.CENTER_CROP // Centriranje i sečenje slike
        }
        rootLayout.addView(backgroundImage)

        // Tamni sloj za zatamnjivanje
        val dimOverlay = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#B3000000")) // 70% crna providnost
        }
        rootLayout.addView(dimOverlay)

        // Glavni LinearLayout za sadržaj
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }


        // Prazan prostor na vrhu (20% visine)
        val topSpacer = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            2f // 20% ukupne visine
        )
        contentLayout.addView(LinearLayout(this).apply {
            layoutParams = topSpacer
        })

        // Lista resursa za slike, aktivnosti i natpise
        val imageData = listOf(
            Triple(R.drawable.training_image, PushTrainingActivity::class.java, "Push training"),
            Triple(R.drawable.exercises_image, PullTrainingActivity::class.java, "Pull training"),
            Triple(R.drawable.muscles_image, ArmsTrainingActivity::class.java, "Arms training"),
            Triple(R.drawable.muscles_image, LegsTrainingActivity::class.java, "Legs training"),
            Triple(R.drawable.muscles_image, ChestTrainingActivity::class.java, "Chest training"),
            Triple(R.drawable.muscles_image, BackTrainingActivity::class.java, "Back training"),
            Triple(R.drawable.muscles_image, BicepsTrainingActivity::class.java, "Biceps training"),
            Triple(R.drawable.muscles_image, TricepsTrainingActivity::class.java, "Triceps training"),
        )

        // Dodavanje slika sa natpisima (60% visine)
        imageData.forEach { (_, targetActivity, title) ->
            val textFrame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    6f / imageData.size // Svaka slika dobija jednaku visinu unutar 60%
                ).apply {
                    setMargins(0, 68, 0, 8) // Razmak između slika
                }
            }


            val titleOverlay = TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                text = title
                textSize = 24f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#80000000"))
                gravity = android.view.Gravity.CENTER
                setOnClickListener {
                    // Otvaranje odgovarajuće stranice
                    val intent = Intent(this@TrainingActivity, targetActivity)
                    startActivity(intent)
                }
            }

            textFrame.addView(titleOverlay)
            contentLayout.addView(textFrame)
        }

        // Prazan prostor na dnu (20% visine)
        val bottomSpacer = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            2f // 20% ukupne visine
        )
        contentLayout.addView(LinearLayout(this).apply {
            layoutParams = bottomSpacer
        })


        // Dodavanje sadržaja u glavni FrameLayout
        rootLayout.addView(contentLayout)

        // Postavljanje sadržaja aktivnosti
        setContentView(rootLayout)
    }
}
