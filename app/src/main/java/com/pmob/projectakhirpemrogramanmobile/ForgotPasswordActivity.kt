package com.pmob.projectakhirpemrogramanmobile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Submit email
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            sendResetEmail(email)
        }
    }

    private fun sendResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Email reset password telah dikirim. Silakan cek email Anda.",
                    Toast.LENGTH_LONG
                ).show()
                finish() // balik ke Login
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message ?: "Gagal mengirim email reset",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
