package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Glavni LinearLayout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Layout za muškarce
        val muskarciLayout = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // Polovina ekrana
            )
            setOnClickListener {
                val intent = Intent(this@MainActivity, MuskarciActivity::class.java)
                startActivity(intent)
            }
        }

        // Slika za muškarce
        val muskarciImage = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setImageResource(R.drawable.main_activity_muskarci) // Postavite odgovarajući resurs
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Tekst za muškarce
        val muskarciText = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            text = "Muškarci"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#80000000")) // Poluprovidna pozadina
            gravity = android.view.Gravity.CENTER
        }

        // Dodavanje slike i teksta u muškarci layout
        muskarciLayout.addView(muskarciImage)
        muskarciLayout.addView(muskarciText)

        // Layout za žene
        val zeneLayout = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // Polovina ekrana
            )
            setOnClickListener {
                setOnClickListener {
                    // Prikazivanje "Coming Soon" dijaloga
                    showComingSoonDialog()
                }
            }
        }

        // Slika za žene
        val zeneImage = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setImageResource(R.drawable.main_activity_zene) // Postavite odgovarajući resurs
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Tekst za žene
        val zeneText = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            text = "Žene"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#80000000")) // Poluprovidna pozadina
            gravity = android.view.Gravity.CENTER
        }

        // Dodavanje slike i teksta u žene layout
        zeneLayout.addView(zeneImage)
        zeneLayout.addView(zeneText)

        // Dodavanje layout-ova u glavni layout
        rootLayout.addView(muskarciLayout)
        rootLayout.addView(zeneLayout)

        // Postavljanje sadržaja aktivnosti
        setContentView(rootLayout)


    }
    // Funkcija za prikazivanje "Coming Soon" dijaloga
    private fun showComingSoonDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Coming Soon")
        builder.setMessage("The women's section is coming soon! Stay tuned.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
