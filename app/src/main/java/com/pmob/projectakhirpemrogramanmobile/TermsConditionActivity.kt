package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pmob.projectakhirpemrogramanmobile.databinding.ActivityTermsConditionBinding

class TermsConditionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default state
        binding.btnContinue.isEnabled = false
        binding.btnContinue.alpha = 0.5f


        binding.cbAgree.setOnCheckedChangeListener { _, isChecked ->
            binding.btnContinue.isEnabled = isChecked
            binding.btnContinue.alpha = if (isChecked) 1f else 0.5f
        }

        binding.btnContinue.setOnClickListener {
            startActivity(
                Intent(this, SetupProfileActivity::class.java)
            )
        }

        binding.btnBackVerif.setOnClickListener {
            val intent = Intent(this, VerifyEmailActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}