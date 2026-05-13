package com.mindmatrix.hallisanthedigital.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.adapter.ProductAdapter
import com.mindmatrix.hallisanthedigital.databinding.ActivitySellerDashboardBinding
import com.mindmatrix.hallisanthedigital.model.Product
import com.mindmatrix.hallisanthedigital.ui.addproduct.AddProductActivity
import com.mindmatrix.hallisanthedigital.ui.auth.LoginActivity
import com.mindmatrix.hallisanthedigital.utils.Constants

class SellerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerDashboardBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val myProducts = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show seller's first name
        val user = auth.currentUser
        binding.tvSellerName.text = user?.displayName?.split(" ")?.firstOrNull() ?: "Seller"

        setupRecyclerView()
        setupButtons()
        loadMyProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            products = mutableListOf(),
            onItemClick = { },  // Sellers don't need to open product detail
            onDeleteClick = { product ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete \"${product.name}\"?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        deleteProduct(product)
                    }
                    .show()
            }
        )
        // Use GridLayoutManager with 2 columns same as HomeActivity
        binding.rvMyProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyProducts.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun loadMyProducts() {
        val sellerId = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        // Removed .orderBy("timestamp") because it requires a manual Composite Index in Firebase
        db.collection(Constants.COLLECTION_PRODUCTS)
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                binding.progressBar.visibility = View.GONE
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                
                myProducts.clear()
                snapshot?.documents?.forEach { doc ->
                    val product = doc.toObject(Product::class.java)?.copy(productId = doc.id)
                    product?.let { myProducts.add(it) }
                }

                // ✅ Sort locally to avoid "Query requires an index" error
                myProducts.sortByDescending { it.timestamp }
                
                adapter.updateList(myProducts)
                updateStats()
                binding.tvEmptyState.visibility =
                    if (myProducts.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun updateStats() {
        binding.tvProductCount.text = myProducts.size.toString()
        val totalValue = myProducts.sumOf { it.price }
        binding.tvTotalValue.text = "₹%.0f".format(totalValue)
    }

    private fun deleteProduct(product: Product) {
        db.collection(Constants.COLLECTION_PRODUCTS)
            .document(product.productId)
            .delete()
            .addOnSuccessListener {
                adapter.removeItem(product)
                myProducts.removeAll { it.productId == product.productId }
                updateStats()
                Toast.makeText(this, "\"${product.name}\" deleted!", Toast.LENGTH_SHORT).show()
                binding.tvEmptyState.visibility =
                    if (myProducts.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
    }
}