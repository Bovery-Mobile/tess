package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityRegisterBinding
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupPasswordToggle(binding.etPassword)
        setupPasswordToggle(binding.etConfirmPassword)

        // ===== Checkbox Terms =====
        binding.btnRegister.isEnabled = false
        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.btnRegister.isEnabled = isChecked
        }

        // ===== Button Register =====
        binding.btnRegister.setOnClickListener {

            if (!binding.cbTerms.isChecked) {
                Toast.makeText(
                    this,
                    "Anda harus menyetujui Syarat & Ketentuan",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.etEmail.error = "Email wajib diisi"
                    return@setOnClickListener
                }

                password.isEmpty() -> {
                    binding.etPassword.error = "Password wajib diisi"
                    return@setOnClickListener
                }

                password.length < 6 -> {
                    binding.etPassword.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }

                confirmPassword.isEmpty() -> {
                    binding.etConfirmPassword.error = "Konfirmasi password wajib diisi"
                    return@setOnClickListener
                }

                password != confirmPassword -> {
                    binding.etConfirmPassword.error = "Password tidak sama"
                    return@setOnClickListener
                }
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Email verifikasi telah dikirim",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this, VerifyEmailActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message ?: "Register gagal",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        val text = "Sudah punya akun? Masuk"
        val spannable = SpannableString(text)

        val startMasuk = text.indexOf("Masuk")
        val endMasuk = startMasuk + "Masuk".length

// 🔹 Warna abu-abu untuk teks biasa
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.text_secondary)
            ),
            0,
            startMasuk,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// 🔹 Warna primary untuk "Masuk"
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.primary)
            ),
            startMasuk,
            endMasuk,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// 🔹 Klik hanya kata "Masuk"
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                finish() // balik ke LoginActivity
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                ds.isUnderlineText = false
            }
        }, startMasuk, endMasuk, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvLogin.text = spannable
        binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLogin.highlightColor = android.graphics.Color.TRANSPARENT


        // ===== Back to Login =====
        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }


    }








    private fun setPasswordVisibility(editText: EditText, visible: Boolean) {
        if (visible) {
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_lock,
                0,
                R.drawable.ic_eye_off,
                0
            )
        } else {
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_lock,
                0,
                R.drawable.ic_eye,
                0
            )
        }

        editText.setSelection(editText.text.length)
    }


    private fun togglePasswordForBoth() {
        isPasswordVisible = !isPasswordVisible

        applyPasswordState(binding.etPassword)
        applyPasswordState(binding.etConfirmPassword)
    }

    private fun applyPasswordState(editText: EditText) {
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
    }

    private fun setupPasswordToggle(editText: EditText) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                // Area icon kanan (STABIL)
                val drawableEnd = editText.compoundDrawables[2] ?: return@setOnTouchListener false
                val iconStartX = editText.width - editText.paddingEnd - drawableEnd.intrinsicWidth

                if (event.x >= iconStartX) {
                    togglePasswordForBoth()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}

