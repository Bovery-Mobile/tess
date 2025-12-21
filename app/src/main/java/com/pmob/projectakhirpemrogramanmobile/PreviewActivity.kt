package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlin.jvm.java


class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val title = intent.getStringExtra("BOOK_TITLE")
        val author = intent.getStringExtra("BOOK_AUTHOR")
        val cover = intent.getStringExtra("BOOK_COVER")
        val rating = intent.getDoubleExtra("BOOK_RATING", 0.0)
        val pages = intent.getIntExtra("BOOK_PAGES", 0)
        val synopsis = intent.getStringExtra("BOOK_SYNOPSIS")

        // ⬅️ BACK BUTTON (INI YANG DITAMBAH)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvAuthor).text = author
        findViewById<TextView>(R.id.tvRating).text =
            String.format("%.1f", rating)
        findViewById<TextView>(R.id.tvPages).text = "$pages"
        findViewById<TextView>(R.id.tvSynopsis).text = synopsis

        Glide.with(this)
            .load(cover)
            .into(findViewById(R.id.ivCover))

        // Tombol Mulai Baca
        findViewById<MaterialButton>(R.id.btnStartRead).setOnClickListener {
            val intent = Intent(this, ReadActivity::class.java)
            intent.putExtra("BOOK_TITLE", title)
            intent.putExtra("BOOK_CONTENT", synopsis) // ⬅ INI WAJIB
            startActivity(intent)
        }

    }
}
