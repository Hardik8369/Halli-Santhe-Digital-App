package com.mindmatrix.hallisanthedigital.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.databinding.ActivitySignupBinding
import com.mindmatrix.hallisanthedigital.ui.home.HomeActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Pre-fill if coming from Google Sign-In
        val isGoogleUser = intent.getBooleanExtra("isGoogleUser", false)
        if (isGoogleUser) {
            binding.etName.setText(intent.getStringExtra("googleName") ?: "")
            binding.etEmail.setText(intent.getStringExtra("googleEmail") ?: "")
            binding.etEmail.isEnabled = false
            binding.etPassword.visibility = View.GONE
            binding.etConfirmPassword.visibility = View.GONE
        }

        binding.btnSignup.setOnClickListener {
            if (isGoogleUser) saveGoogleUserProfile()
            else registerWithEmail()
        }

        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun registerWithEmail() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val role = if (binding.rbBuyer.isChecked) "buyer" else "seller"

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
            phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                saveUserToFirestore(uid, name, email, phone, address, role)
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveGoogleUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val role = if (binding.rbBuyer.isChecked) "buyer" else "seller"

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        saveUserToFirestore(uid, name, email, phone, address, role)
    }

    private fun saveUserToFirestore(
        uid: String, name: String, email: String,
        phone: String, address: String, role: String
    ) {
        val user = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "address" to address,
            "role" to role,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Welcome, $name! 🎉", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to save profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignup.isEnabled = !show
    }
}