package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityDetailBookBinding

class DetailBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_book)

        // Ambil data dari Intent
        val title = intent.getStringExtra("BOOK_TITLE") ?: "Unknown"
        val author = intent.getStringExtra("BOOK_AUTHOR") ?: "Unknown"
        val coverUrl = intent.getStringExtra("BOOK_COVER") ?: ""
        val rating = intent.getDoubleExtra("BOOK_RATING", 0.0)
        val year = intent.getIntExtra("BOOK_YEAR", 0)
        val pages = intent.getIntExtra("BOOK_PAGES", 0)
        val synopsis = intent.getStringExtra("BOOK_SYNOPSIS") ?: "No description"
        val price = intent.getDoubleExtra("BOOK_PRICE", 0.0)
        val genres = intent.getStringArrayListExtra("BOOK_GENRES") ?: arrayListOf()

        setupViews(
            title,
            author,
            coverUrl,
            rating,
            year,
            pages,
            synopsis,
            genres
        )

        setupListeners(
            title,
            author,
            coverUrl,
            rating,
            pages,
            synopsis,
            price
        )
    }

    private fun setupViews(
        title: String,
        author: String,
        coverUrl: String,
        rating: Double,
        year: Int,
        pages: Int,
        synopsis: String,
        genres: ArrayList<String>
    ) {
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.tvBookTitle).text = title
        findViewById<TextView>(R.id.tvAuthor).text = author
        findViewById<TextView>(R.id.tvRating).text = String.format("%.1f", rating)
        findViewById<TextView>(R.id.tvPublished).text = year.toString()
        findViewById<TextView>(R.id.tvPages).text = pages.toString()
        findViewById<TextView>(R.id.tvSynopsis).text = synopsis

        Glide.with(this)
            .load(coverUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(findViewById(R.id.ivBookCover))

        val chipGroup = findViewById<ChipGroup>(R.id.genreChipGroup)
        chipGroup.removeAllViews()
        genres.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.isClickable = false
            chip.isCheckable = false
            chipGroup.addView(chip)
        }

        val tvSynopsis = findViewById<TextView>(R.id.tvSynopsis)
        val tvReadMore = findViewById<TextView>(R.id.tvReadMore)

        tvSynopsis.maxLines = 4
        tvReadMore.setOnClickListener {
            if (tvSynopsis.maxLines == 4) {
                tvSynopsis.maxLines = Int.MAX_VALUE
                tvReadMore.text = "Read Less ▲"
            } else {
                tvSynopsis.maxLines = 4
                tvReadMore.text = "Read More ▼"
            }
        }
    }

    private fun setupListeners(
        title: String,
        author: String,
        coverUrl: String,
        rating: Double,
        pages: Int,
        synopsis: String,
        price: Double
    ) {
        findViewById<ImageView>(R.id.ivShare).setOnClickListener {
            Toast.makeText(this, "Share coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.ivBookmark).setOnClickListener {
            Toast.makeText(this, "Added to bookmark", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnBuy).apply {
            text = "Buy $${String.format("%.2f", price)}"
            setOnClickListener {
                val intent = Intent(this@DetailBookActivity, BuyActivity::class.java)
                intent.putExtra("BOOK_TITLE", title)
                intent.putExtra("BOOK_PRICE", price)
                intent.putExtra("BOOK_COVER", coverUrl) // ⬅️ INI SAJA


                startActivity(intent)
            }
        }

        findViewById<MaterialButton>(R.id.btnReadNow).setOnClickListener {
            val intent = Intent(this@DetailBookActivity, PreviewActivity::class.java)
            intent.putExtra("BOOK_TITLE", title)
            intent.putExtra("BOOK_AUTHOR", author)
            intent.putExtra("BOOK_COVER", coverUrl)
            intent.putExtra("BOOK_RATING", rating)
            intent.putExtra("BOOK_PAGES", pages)
            intent.putExtra("BOOK_SYNOPSIS", synopsis)
            startActivity(intent)
        }
    }
}

