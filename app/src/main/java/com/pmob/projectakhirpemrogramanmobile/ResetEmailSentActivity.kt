package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityResetEmailSentBinding

class ResetEmailSentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetEmailSentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetEmailSentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali ke Login
        binding.btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
