package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivitySetupProfileBinding
import android.app.DatePickerDialog
import java.util.Calendar


class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ===== Toolbar Back =====
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ===== Spinner Jenis Kelamin =====
        val genderList = listOf(
            "Pilih Jenis Kelamin",
            "Laki-laki",
            "Perempuan"
        )

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            genderList
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spGender.adapter = adapter
        binding.spGender.setSelection(0)

        // ===== Date Picker =====
        setupDatePicker()

        // ===== Button Simpan =====
        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    // ✅ FUNCTION HARUS DI SINI
    private fun setupDatePicker() {
        binding.etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val date = String.format(
                        "%02d/%02d/%04d",
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    binding.etBirthDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Optional: tidak bisa pilih tanggal masa depan
            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: ""

        val fullName = binding.etFullName.text.toString().trim()
        val gender = binding.spGender.selectedItem.toString()
        val birthPlace = binding.etBirthPlace.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val hobby = binding.etHobby.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama wajib diisi"
            return
        }

        if (gender == "Pilih Jenis Kelamin") {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()
            return
        }

        if (birthDate.isEmpty()) {
            binding.etBirthDate.error = "Tanggal lahir wajib diisi"
            return
        }

        val userData = mapOf(
            "fullName" to fullName,
            "gender" to gender,
            "birthPlace" to birthPlace,
            "birthDate" to birthDate,
            "address" to address,
            "hobby" to hobby,
            "bio" to bio,
            "email" to email
        )

        database.child("users").child(uid)
            .setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
            }
    }
}
