package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityPaymentBinding
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    private var selectedMethod: String? = null
    private var bookTitle = ""
    private var bookPrice = 0.0
    private var bookCover: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ===== Ambil data dari intent =====
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""
        bookPrice = intent.getDoubleExtra("BOOK_PRICE", 0.0)
        bookCover = intent.getStringExtra("BOOK_COVER")

        // ===== Set UI =====
        binding.tvTitle.text = bookTitle
        binding.tvPrice.text = formatRupiah(bookPrice)
        binding.tvTotal.text = formatRupiah(bookPrice)

        Glide.with(this)
            .load(bookCover)
            .placeholder(R.drawable.ic_book_placeholder)
            .error(R.drawable.ic_book_placeholder)
            .into(binding.ivCover)

        // ===== Klik Card (lebih UX friendly) =====
        binding.layoutTransfer.setOnClickListener {
            binding.rbTransfer.isChecked = true
            selectedMethod = "Transfer Bank"
        }

        binding.layoutEwallet.setOnClickListener {
            binding.rbEwallet.isChecked = true
            selectedMethod = "E-Wallet"
        }

        // ===== Radio Change Listener =====
        binding.rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            resetHighlight()

            when (checkedId) {
                R.id.rbTransfer -> {
                    selectedMethod = "Transfer Bank"
                    binding.layoutTransfer.setBackgroundResource(R.drawable.bg_card_selected)
                }
                R.id.rbEwallet -> {
                    selectedMethod = "E-Wallet"
                    binding.layoutEwallet.setBackgroundResource(R.drawable.bg_card_selected)
                }
            }
        }

        // ===== Bayar =====
        binding.btnPayNow.setOnClickListener {
            if (selectedMethod == null) {
                Toast.makeText(
                    this,
                    "Pilih metode pembayaran terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                processPayment()
            }
        }
    }

    // ================== RESET CARD ==================
    private fun resetHighlight() {
        binding.layoutTransfer.setBackgroundResource(R.drawable.bg_card)
        binding.layoutEwallet.setBackgroundResource(R.drawable.bg_card)
    }

    // ================== PAYMENT DUMMY ==================
    private fun processPayment() {
        savePurchaseToFirebase()

        Toast.makeText(
            this,
            "Pembayaran berhasil ($selectedMethod)",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, SuccessActivity::class.java)
        intent.putExtra("BOOK_TITLE", bookTitle)
        intent.putExtra("BOOK_PRICE", formatRupiah(bookPrice))
        intent.putExtra("PAYMENT_METHOD", selectedMethod)
        startActivity(intent)
        finish()
    }

    // ================== SAVE TO FIREBASE ==================
    private fun savePurchaseToFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseDatabase.getInstance()
            .getReference("purchases")
            .child(uid)

        val purchaseId = db.push().key ?: return

        val purchase = Purchase(
            title = bookTitle,
            price = bookPrice,
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS",
            method = selectedMethod ?: "-"
        )

        db.child(purchaseId).setValue(purchase)
    }

    // ================== FORMAT RUPIAH ==================
    private fun formatRupiah(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value)
    }
}
