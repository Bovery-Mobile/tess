package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityBuyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding

    private var bookTitle: String = ""
    private var bookPrice: Double = 0.0
    private var bookCover: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ===== Ambil data dari Intent =====
        bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""
        bookPrice = intent.getDoubleExtra("BOOK_PRICE", 0.0)
        bookCover = intent.getStringExtra("BOOK_COVER")

        // ===== Set UI =====
        binding.tvTitle.text = bookTitle
        binding.tvPrice.text = formatRupiah(bookPrice)

        Glide.with(this)
            .load(bookCover)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivCover)

        // ===== Midtrans Callback =====
        MidtransSDK.getInstance().setTransactionFinishedCallback { result ->

            if (result.response != null) {
                when (result.status) {

                    TransactionResult.STATUS_SUCCESS -> {
                        savePurchaseToFirebase(bookTitle, bookPrice)

                        val intent = Intent(this, SuccessActivity::class.java)
                        intent.putExtra("BOOK_TITLE", bookTitle)
                        intent.putExtra("BOOK_PRICE", formatRupiah(bookPrice))
                        startActivity(intent)
                        finish()
                    }

                    TransactionResult.STATUS_PENDING -> {
                        Toast.makeText(this, "Menunggu pembayaran", Toast.LENGTH_SHORT).show()
                    }

                    TransactionResult.STATUS_FAILED -> {
                        Toast.makeText(this, "Pembayaran gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (result.isTransactionCanceled) {
                Toast.makeText(this, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }



        // ===== Actions =====
        binding.btnBack.setOnClickListener { finish() }

        binding.btnBuyNow.setOnClickListener {
            requestSnapToken()
        }
    }

    // ================== REQUEST TOKEN ==================
    private fun requestSnapToken() {
        MidtransRetrofit.api.getSnapToken()
            .enqueue(object : Callback<MidtransResponse> {
                override fun onResponse(
                    call: Call<MidtransResponse>,
                    response: Response<MidtransResponse>
                ) {
                    val token = response.body()?.token
                    if (response.isSuccessful && token != null) {
                        MidtransSDK.getInstance().startPaymentUiFlow(this@BuyActivity, token)
                    } else {
                        Toast.makeText(this@BuyActivity, "Gagal ambil token", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MidtransResponse>, t: Throwable) {
                    Toast.makeText(this@BuyActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ================== SAVE TO FIREBASE ==================
    private fun savePurchaseToFirebase(title: String, price: Double) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseDatabase.getInstance()
            .getReference("purchases")
            .child(uid)

        val purchaseId = db.push().key ?: return

        val purchase = Purchase(
            title = title,
            price = price,
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS"
        )

        db.child(purchaseId).setValue(purchase)
    }


    // ================== FORMAT RUPIAH ==================
    private fun formatRupiah(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(value)
    }
}
