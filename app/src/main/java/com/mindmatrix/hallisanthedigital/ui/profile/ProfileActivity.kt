package com.mindmatrix.hallisanthedigital.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.databinding.ActivityProfileBinding
import com.mindmatrix.hallisanthedigital.ui.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadProfile()

        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        showLoading(true)

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                showLoading(false)
                if (doc.exists()) {
                    binding.etName.setText(doc.getString("name") ?: "")
                    binding.etPhone.setText(doc.getString("phone") ?: "")
                    binding.etAddress.setText(doc.getString("address") ?: "")
                    binding.tvEmail.text = doc.getString("email") ?: auth.currentUser?.email ?: ""
                    binding.tvRole.text = "Role: ${(doc.getString("role") ?: "").replaceFirstChar { it.uppercase() }}"
                }
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        val updates = mapOf(
            "name" to name,
            "phone" to phone,
            "address" to address
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Profile updated successfully ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to update: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !show
    }
}