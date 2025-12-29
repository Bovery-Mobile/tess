package com.pmob.projectakhirpemrogramanmobile.ui.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pmob.projectakhirpemrogramanmobile.EditProfileActivity
import com.pmob.projectakhirpemrogramanmobile.LoginActivity
import com.pmob.projectakhirpemrogramanmobile.R
import com.pmob.projectakhirpemrogramanmobile.databinding.FragmentProfileBinding
import com.pmob.projectakhirpemrogramanmobile.utils.ImageUtils

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    // ===================== GALLERY =====================
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.imgProfile.setImageURI(it)
                savePhotoToFirebase(it)
            }
        }

    // ===================== CAMERA =====================
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = ImageUtils.saveBitmapToCache(requireContext(), it)
                binding.imgProfile.setImageURI(uri)
                savePhotoToFirebase(uri)
            }
        }

    // ===================== CAMERA PERMISSION =====================
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin kamera diperlukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // ===================== LIFECYCLE =====================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileFromLocal()
        syncProfileFromFirebase()
        setupActions()
    }

    override fun onResume() {
        super.onResume()
        loadProfileFromLocal()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===================== ACTIONS =====================
    private fun setupActions() {

        // Back → Home (navbar)
        binding.btnBack.setOnClickListener {
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNav)
                .selectedItemId = R.id.nav_home
        }

        // Edit Profile
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // Change Photo
        binding.btnChangePhoto.setOnClickListener {
            showPhotoOptionDialog()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()

            requireContext()
                .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    // ===================== PHOTO OPTION =====================
    private fun showPhotoOptionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Foto Profil")
            .setItems(arrayOf("Galeri", "Kamera")) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> openCameraWithPermission()
                }
            }
            .show()
    }

    // ===================== CAMERA PERMISSION CHECK =====================
    private fun openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ===================== SAVE PHOTO =====================
    private fun savePhotoToFirebase(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profile_photos/$uid.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    if (_binding == null) return@addOnSuccessListener

                    database.child("users")
                        .child(uid)
                        .child("photo")
                        .setValue(downloadUrl.toString())

                    requireContext()
                        .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                        .edit()
                        .putString("photo", downloadUrl.toString())
                        .apply()
                }
            }
    }

    // ===================== LOAD LOCAL =====================
    private fun loadProfileFromLocal() {
        val prefs = requireContext()
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        binding.tvName.text = prefs.getString("fullName", "User")
        binding.tvBio.text = prefs.getString("bio", "-")

        prefs.getString("photo", null)?.let {
            binding.imgProfile.setImageURI(Uri.parse(it))
        }
    }

    // ===================== SYNC FIREBASE =====================
    private fun syncProfileFromFirebase() {
        val uid = auth.currentUser?.uid ?: return

        database.child("users").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val fullName = snapshot.child("fullName").value?.toString()
                val bio = snapshot.child("bio").value?.toString()
                val photo = snapshot.child("photo").value?.toString()

                val prefs = requireContext()
                    .getSharedPreferences("user_profile", Context.MODE_PRIVATE)

                prefs.edit().apply {
                    fullName?.let {
                        binding.tvName.text = it
                        putString("fullName", it)
                    }
                    bio?.let {
                        binding.tvBio.text = it
                        putString("bio", it)
                    }
                    photo?.let {
                        binding.imgProfile.setImageURI(Uri.parse(it))
                        putString("photo", it)
                    }
                    apply()
                }
            }
    }
}
