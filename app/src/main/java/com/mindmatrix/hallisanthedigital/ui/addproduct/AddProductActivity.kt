package com.mindmatrix.hallisanthedigital.ui.addproduct

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.databinding.ActivityAddProductBinding
import com.mindmatrix.hallisanthedigital.model.Product
import com.mindmatrix.hallisanthedigital.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val categories = listOf("Food", "Craft", "Textile", "Other")
    private var selectedImageUri: Uri? = null

    companion object {
        const val IMAGE_PICK_CODE = 100
        private const val TAG = "GEMINI_DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupCategoryDropdown()

        binding.btnSelectImage.setOnClickListener { pickImage() }
        binding.btnGenerateDesc.setOnClickListener { generateDescription() }
        binding.btnSaveProduct.setOnClickListener { saveProduct() }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.spinnerCategory.setAdapter(adapter)
        binding.spinnerCategory.setText(categories[0], false)
    }

    private fun pickImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imgProduct.setImageURI(selectedImageUri)
            binding.imgProduct.visibility = View.VISIBLE
        }
    }

    private fun generateDescription() {
        val productName = binding.etProductName.text.toString().trim()
        val apiKey = Constants.GEMINI_API_KEY

        if (productName.isEmpty()) {
            Toast.makeText(this, "Please enter product name first!", Toast.LENGTH_SHORT).show()
            return
        }

        if (apiKey.isEmpty() || apiKey.contains("YOUR_GEMINI")) {
            Toast.makeText(this, "API Key missing! Sync Gradle in Android Studio.", Toast.LENGTH_LONG).show()
            return
        }

        binding.btnGenerateDesc.isEnabled = false
        binding.btnGenerateDesc.text = "Generating..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val prompt = "Write a short 2-3 sentence description for $productName."

                // Gemini Body structure
                val json = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", prompt) })
                            })
                        })
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)
                val url = "${Constants.GEMINI_URL}?key=$apiKey"
                
                Log.d(TAG, "URL: $url")

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val text = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        binding.etDescription.setText(text)
                    } else {
                        // Extract specific error message from Google
                        val detailedError = try {
                            val errorJson = JSONObject(responseBody ?: "")
                            if (errorJson.has("error")) {
                                errorJson.getJSONObject("error").getString("message")
                            } else {
                                "Code ${response.code}: Bad Request"
                            }
                        } catch (e: Exception) {
                            "Code ${response.code}: ${response.message}"
                        }

                        Log.e(TAG, "Full Error: $responseBody")
                        Toast.makeText(this@AddProductActivity, "AI Error: $detailedError", Toast.LENGTH_LONG).show()
                    }
                    binding.btnGenerateDesc.isEnabled = true
                    binding.btnGenerateDesc.text = "✨ Generate Description with AI"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Exception", e)
                    Toast.makeText(this@AddProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.btnGenerateDesc.isEnabled = true
                    binding.btnGenerateDesc.text = "✨ Generate Description with AI"
                }
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val phone = binding.etSellerPhone.text.toString().trim()
        val category = binding.spinnerCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty() || priceStr.isEmpty() || phone.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null) {
            Toast.makeText(this, "Please enter a valid price!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a product image!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProduct.isEnabled = false
        binding.btnSaveProduct.text = "Uploading..."

        MediaManager.get()
            .upload(selectedImageUri)
            .option("folder", "halli_santhe")
            .unsigned("xo9o8bi9")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"].toString()
                    saveToFirestore(name, price, phone, category, description, imageUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSaveProduct.isEnabled = true
                        binding.btnSaveProduct.text = "Save Product"
                        Toast.makeText(this@AddProductActivity, "Image upload failed: ${error.description}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

    private fun saveToFirestore(
        name: String,
        price: Double,
        phone: String,
        category: String,
        description: String,
        imageUrl: String
    ) {
        val currentUser = auth.currentUser
        val sellerId = currentUser?.uid ?: ""
        val sellerName = currentUser?.displayName ?: ""

        val product = Product(
            name = name,
            price = price,
            category = category,
            description = description,
            imageUrl = imageUrl,
            sellerPhone = phone,
            sellerName = sellerName,
            sellerId = sellerId,
            timestamp = System.currentTimeMillis()
        )

        db.collection(Constants.COLLECTION_PRODUCTS)
            .add(product)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Product added successfully! 🎉", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.btnSaveProduct.isEnabled = true
                binding.btnSaveProduct.text = "Save Product"
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}