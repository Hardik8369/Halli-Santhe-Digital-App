package com.mindmatrix.hallisanthedigital.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.R
import com.mindmatrix.hallisanthedigital.adapter.ProductAdapter
import com.mindmatrix.hallisanthedigital.databinding.ActivityHomeBinding
import com.mindmatrix.hallisanthedigital.model.Product
import com.mindmatrix.hallisanthedigital.ui.addproduct.AddProductActivity
import com.mindmatrix.hallisanthedigital.ui.auth.LoginActivity
import com.mindmatrix.hallisanthedigital.ui.productdetail.ProductDetailActivity
import com.mindmatrix.hallisanthedigital.ui.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var adapter: ProductAdapter? = null  // ✅ nullable to avoid crash
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var allProducts = mutableListOf<Product>()
    private var selectedCategory = "All"
    private var userRole = "buyer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // ✅ Setup RecyclerView immediately so adapter is ready before Firestore fires
        setupRecyclerView()
        setupChips()
        setupSearch()

        // ✅ Load user role then update UI accordingly
        loadUserRoleAndSetup()

        // ✅ Load products immediately (adapter is already initialized above)
        loadProducts()
    }

    private fun loadUserRoleAndSetup() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            goToLogin()
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                userRole = doc.getString("role") ?: "buyer"
                val userName = doc.getString("name") ?: "User"

                supportActionBar?.subtitle = if (userRole == "seller")
                    "👨‍🌾 Seller: $userName" else "🛒 Buyer: $userName"

                // ✅ Show/hide FAB based on role
                if (userRole == "seller") {
                    binding.fabAddProduct.visibility = View.VISIBLE
                    binding.fabAddProduct.setOnClickListener {
                        startActivity(Intent(this, AddProductActivity::class.java))
                    }
                } else {
                    binding.fabAddProduct.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                // fallback - just hide FAB
                binding.fabAddProduct.visibility = View.GONE
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Logout") { _, _ ->
                        auth.signOut()
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        goToLogin()
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            mutableListOf(),
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("productId", product.productId)
                    putExtra("name", product.name)
                    putExtra("price", product.price)
                    putExtra("category", product.category)
                    putExtra("description", product.description)
                    putExtra("imageUrl", product.imageUrl)
                    putExtra("sellerPhone", product.sellerPhone)
                }
                startActivity(intent)
            },
            onDeleteClick = { product ->
                if (userRole == "seller") {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete \"${product.name}\"?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete") { _, _ ->
                            deleteProduct(product)
                        }
                        .show()
                } else {
                    Toast.makeText(this, "Only sellers can delete products", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter
    }

    private fun deleteProduct(product: Product) {
        db.collection("products")
            .document(product.productId)
            .delete()
            .addOnSuccessListener {
                allProducts.removeAll { it.productId == product.productId }
                adapter?.removeItem(product)
                filterProducts(binding.searchView.query.toString())
                Toast.makeText(this, "\"${product.name}\" deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupChips() {
        binding.chipAll.setOnClickListener { filterByCategory("All") }
        binding.chipFood.setOnClickListener { filterByCategory("Food") }
        binding.chipCraft.setOnClickListener { filterByCategory("Craft") }
        binding.chipTextile.setOnClickListener { filterByCategory("Textile") }
        binding.chipOther.setOnClickListener { filterByCategory("Other") }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })
    }

    private fun filterByCategory(category: String) {
        selectedCategory = category
        filterProducts(binding.searchView.query.toString())
    }

    private fun filterProducts(query: String) {
        // ✅ Guard against adapter not being ready
        if (adapter == null) return

        val filtered = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
            val matchesSearch = product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        adapter?.updateList(filtered)
        binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun loadProducts() {
        db.collection("products")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    allProducts.clear()
                    for (doc in snapshot.documents) {
                        val product = doc.toObject(Product::class.java)
                        if (product != null) {
                            allProducts.add(product.copy(productId = doc.id))
                        }
                    }
                    filterProducts(binding.searchView.query.toString())
                }
            }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }
}