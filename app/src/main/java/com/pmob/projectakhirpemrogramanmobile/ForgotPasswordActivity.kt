package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
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

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Mengirim..."

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                val intent = Intent(this, ResetEmailSentActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Kirim Email Reset"

                val message = when {
                    error.message?.contains("no user record", true) == true ->
                        "Email tidak terdaftar di Bovery"
                    else ->
                        "Gagal mengirim email. Periksa koneksi Anda."
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
    }

}
