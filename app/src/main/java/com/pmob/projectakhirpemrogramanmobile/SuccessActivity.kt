package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("BOOK_TITLE") ?: "-"
        val price = intent.getStringExtra("BOOK_PRICE") ?: "-"

        binding.tvSuccessTitle.text = title
        binding.tvSuccessPrice.text = price

        // tombol manual
        binding.btnDone.setOnClickListener {
            goHome()
        }

        // auto balik ke home (3 detik)
        Handler(Looper.getMainLooper()).postDelayed({
            goHome()
        }, 3000)
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
