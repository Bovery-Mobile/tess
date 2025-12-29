package com.pmob.projectakhirpemrogramanmobile

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivitySetupProfileBinding
import com.pmob.projectakhirpemrogramanmobile.utils.ImageUtils
import java.util.Calendar

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var imageUri: Uri? = null

    // ===================== USER PREFS (PER UID) =====================
    private fun prefsName(): String {
        val uid = auth.currentUser?.uid ?: ""
        return "user_profile_$uid"
    }

    // ===================== GALLERY =====================
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                binding.imgProfile.setImageURI(it)
                uploadPhoto(it)
            }
        }

    // ===================== CAMERA =====================
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = ImageUtils.saveBitmapToCache(this, it)
                imageUri = uri
                binding.imgProfile.setImageURI(uri)
                uploadPhoto(uri)
            }
        }

    // ===================== LIFECYCLE =====================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        loadProfile()
        setupActions()
    }

    // ===================== LOAD PROFILE =====================
    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        val prefs = getSharedPreferences(prefsName(), Context.MODE_PRIVATE)

        binding.etFullName.setText(prefs.getString("fullName", ""))
        binding.etBirthPlace.setText(prefs.getString("birthPlace", ""))
        binding.etBirthDate.setText(prefs.getString("birthDate", ""))
        binding.etAddress.setText(prefs.getString("address", ""))
        binding.etHobby.setText(prefs.getString("hobby", ""))
        binding.etBio.setText(prefs.getString("bio", ""))

        when (prefs.getString("gender", "")) {
            "Laki-laki" -> binding.rbMale.isChecked = true
            "Perempuan" -> binding.rbFemale.isChecked = true
        }

        prefs.getString("photo", null)?.let {
            binding.imgProfile.setImageURI(Uri.parse(it))
        }

        // 🔄 Firebase (source of truth)
        database.child("users").child(uid)
            .get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) return@addOnSuccessListener

                binding.etFullName.setText(snap.child("fullName").value?.toString() ?: "")
                binding.etBirthPlace.setText(snap.child("birthPlace").value?.toString() ?: "")
                binding.etBirthDate.setText(snap.child("birthDate").value?.toString() ?: "")
                binding.etAddress.setText(snap.child("address").value?.toString() ?: "")
                binding.etHobby.setText(snap.child("hobby").value?.toString() ?: "")
                binding.etBio.setText(snap.child("bio").value?.toString() ?: "")

                when (snap.child("gender").value?.toString()) {
                    "Laki-laki" -> binding.rbMale.isChecked = true
                    "Perempuan" -> binding.rbFemale.isChecked = true
                }

                snap.child("photo").value?.toString()?.let {
                    binding.imgProfile.setImageURI(Uri.parse(it))
                }
            }
    }

    // ===================== DATE PICKER =====================
    private fun setupDatePicker() {
        binding.etBirthDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, y, m, d ->
                    binding.etBirthDate.setText(
                        String.format("%02d/%02d/%04d", d, m + 1, y)
                    )
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
                show()
            }
        }
    }

    // ===================== ACTIONS =====================
    private fun setupActions() {
        binding.btnBack.setOnClickListener { finish() }

        // 📷 Klik teks "Ubah Foto Profil"
        binding.btnChangePhoto.setOnClickListener {
            showPhotoOptionDialog()
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    // ===================== PHOTO OPTION =====================
    private fun showPhotoOptionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Pilih Foto Profil")
            .setItems(arrayOf("Galeri", "Kamera")) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> cameraLauncher.launch(null)
                }
            }
            .show()
    }

    // ===================== UPLOAD PHOTO =====================
    private fun uploadPhoto(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        val ref = FirebaseStorage.getInstance()
            .reference
            .child("profile_photos/$uid.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->

                    // Firebase Database
                    database.child("users")
                        .child(uid)
                        .child("photo")
                        .setValue(url.toString())

                    // Local cache
                    getSharedPreferences(prefsName(), MODE_PRIVATE)
                        .edit()
                        .putString("photo", url.toString())
                        .apply()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal upload foto", Toast.LENGTH_SHORT).show()
            }
    }

    // ===================== SAVE PROFILE =====================
    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: ""

        val fullName = binding.etFullName.text.toString().trim()
        val birthPlace = binding.etBirthPlace.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val hobby = binding.etHobby.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        val gender = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "Laki-laki"
            R.id.rbFemale -> "Perempuan"
            else -> ""
        }

        if (fullName.isEmpty() || gender.isEmpty() ||
            birthPlace.isEmpty() || birthDate.isEmpty() || address.isEmpty()
        ) {
            Toast.makeText(this, "Lengkapi semua data wajib", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf(
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
            .updateChildren(data)
            .addOnSuccessListener {

                getSharedPreferences(prefsName(), MODE_PRIVATE)
                    .edit()
                    .apply {
                        putString("fullName", fullName)
                        putString("gender", gender)
                        putString("birthPlace", birthPlace)
                        putString("birthDate", birthDate)
                        putString("address", address)
                        putString("hobby", hobby)
                        putString("bio", bio)
                        putString("email", email)
                        apply()
                    }

                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
