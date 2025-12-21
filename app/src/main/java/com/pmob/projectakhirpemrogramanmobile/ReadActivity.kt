package com.pmob.projectakhirpemrogramanmobile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ReadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        val title = intent.getStringExtra("BOOK_TITLE") ?: ""
        val content = intent.getStringExtra("BOOK_CONTENT") ?: "Preview content..."

        findViewById<TextView>(R.id.tvReadTitle).text = title
        findViewById<TextView>(R.id.tvReadContent).text = content

        findViewById<ImageView>(R.id.ivBackRead).setOnClickListener {
            finish()
        }
    }
}
