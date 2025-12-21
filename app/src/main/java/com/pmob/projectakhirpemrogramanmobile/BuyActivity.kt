package com.pmob.projectakhirpemrogramanmobile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale


class BuyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)

        // Data dari Intent
        val title = intent.getStringExtra("BOOK_TITLE") ?: "Unknown Book"
        val price = intent.getDoubleExtra("BOOK_PRICE", 0.0)
        val cover = intent.getStringExtra("BOOK_COVER")

        // Views (SESUIAI XML BARU)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)

        val ivCover = findViewById<ImageView>(R.id.ivCover)

        Glide.with(this)
            .load(cover)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(ivCover)


        // Set data
        tvTitle.text = title
        tvPrice.text = formatRupiah(price)

        // Back
        btnBack.setOnClickListener {
            finish()
        }

        // Optional: tombol beli sekarang (kalau mau)
        findViewById<MaterialButton?>(R.id.btnBuyNow)?.setOnClickListener {
            Toast.makeText(this, "Checkout berhasil (dummy)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatRupiah(value: Double): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        return formatter.format(value)
    }
}
