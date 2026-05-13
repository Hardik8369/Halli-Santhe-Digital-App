package com.mindmatrix.hallisanthedigital.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.R
import com.mindmatrix.hallisanthedigital.databinding.ActivitySplashBinding
import com.mindmatrix.hallisanthedigital.ui.auth.LoginActivity
import com.mindmatrix.hallisanthedigital.ui.home.BuyerHomeActivity
import com.mindmatrix.hallisanthedigital.ui.home.SellerDashboardActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        binding.imgLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(slideUp)
        binding.tvTagline.startAnimation(slideUp)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                routeByRole()  // ✅ Route based on role
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2500)
    }

    private fun routeByRole() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: "buyer"
                if (role == "seller") {
                    startActivity(Intent(this, SellerDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, BuyerHomeActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                // Default to buyer if Firestore fails
                startActivity(Intent(this, BuyerHomeActivity::class.java))
                finish()
            }
    }
}