package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ZeneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Kreiranje glavnog layout-a sa RelativeLayout za pozadinu
        val rootLayout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Dodavanje slike pozadine (ovde staviti pravi URI ili Drawable resurs)
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.main_activity_muskarci) // Postavite svoj resurs slike ovde
            scaleType = ImageView.ScaleType.CENTER_CROP // Održava proporcije slike bez razvučenja
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Dodavanje zatamnjenja
        val darkLayer = FrameLayout(this).apply {
            setBackgroundColor(Color.argb(153, 0, 0, 0)) // 60% zatamnjenje
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Postavljamo rootLayout kao sadržaj aktivnosti
        setContentView(rootLayout)
    }
}
