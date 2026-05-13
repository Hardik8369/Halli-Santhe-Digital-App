package com.mindmatrix.hallisanthedigital.ui.productdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mindmatrix.hallisanthedigital.R
import com.mindmatrix.hallisanthedigital.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadProductDetails()
    }

    private fun loadProductDetails() {
        val name = intent.getStringExtra("name") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val category = intent.getStringExtra("category") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val sellerPhone = intent.getStringExtra("sellerPhone") ?: ""

        binding.tvProductName.text = name
        binding.tvPrice.text = "₹$price"
        binding.tvCategory.text = category
        binding.tvDescription.text = description

        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(binding.imgProduct)
        }

        binding.btnContactSeller.setOnClickListener {
            if (sellerPhone.isNotEmpty()) {
                val message = "Hi! I'm interested in your product: $name (₹$price). Is it available?"
                val uri = Uri.parse("https://wa.me/91$sellerPhone?text=${Uri.encode(message)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "WhatsApp not installed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Seller contact not available!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}