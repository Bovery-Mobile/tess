package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth   // 🔥 Firebase Auth
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupPasswordToggle(binding.etPassword)


        // ===== Button Login =====
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Kata sandi wajib diisi"
                return@setOnClickListener
            }

            // 🔐 LOGIN FIREBASE
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        this,
                        error.message ?: "Login gagal",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        val text = "Belum punya akun? Daftar"
        val spannable = SpannableString(text)

        val startDaftar = text.indexOf("Daftar")
        val endDaftar = startDaftar + "Daftar".length

// 🔹 Warna abu untuk teks biasa
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.text_secondary)
            ),
            0,
            startDaftar,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// 🔹 Warna primary untuk "Daftar"
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.primary)
            ),
            startDaftar,
            endDaftar,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// 🔹 Klik hanya di kata "Daftar"
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            // ❌ Hilangkan underline
            override fun updateDrawState(ds: android.text.TextPaint) {
                ds.isUnderlineText = false
            }
        }, startDaftar, endDaftar, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvRegister.text = spannable
        binding.tvRegister.movementMethod = LinkMovementMethod.getInstance()
        binding.tvRegister.highlightColor = android.graphics.Color.TRANSPARENT



        // ===== Ke Register =====
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // ===== Lupa Kata Sandi =====
        binding.tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupPasswordToggle(editText: EditText) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val drawableEnd = editText.compoundDrawables[2]
                    ?: return@setOnTouchListener false

                val iconStartX =
                    editText.width - editText.paddingEnd - drawableEnd.intrinsicWidth

                if (event.x >= iconStartX) {
                    isPasswordVisible = !isPasswordVisible

                    if (isPasswordVisible) {
                        editText.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_lock, 0, R.drawable.ic_eye_off, 0
                        )
                    } else {
                        editText.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_lock, 0, R.drawable.ic_eye, 0
                        )
                    }

                    editText.setSelection(editText.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}
