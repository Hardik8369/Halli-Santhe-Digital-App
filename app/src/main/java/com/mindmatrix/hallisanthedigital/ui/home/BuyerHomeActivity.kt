package com.mindmatrix.hallisanthedigital.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mindmatrix.hallisanthedigital.adapter.ProductAdapter
import com.mindmatrix.hallisanthedigital.databinding.ActivityBuyerHomeBinding
import com.mindmatrix.hallisanthedigital.model.Product
import com.mindmatrix.hallisanthedigital.ui.auth.LoginActivity
import com.mindmatrix.hallisanthedigital.ui.productdetail.ProductDetailActivity
import com.mindmatrix.hallisanthedigital.utils.Constants

class BuyerHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val allProducts = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter
    private var selectedCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show buyer's first name
        val user = auth.currentUser
        binding.tvWelcome.text = "Hello, ${user?.displayName?.split(" ")?.firstOrNull() ?: "Buyer"} 👋"

        setupRecyclerView()
        setupCategoryChips()
        setupSearch()
        setupLogout()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            products = mutableListOf(),
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
            onDeleteClick = { }  // Buyers cannot delete — no-op
        )
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter
    }

    private fun setupCategoryChips() {
        binding.chipAll.setOnClickListener     { filterByCategory("All") }
        binding.chipFood.setOnClickListener    { filterByCategory("Food") }
        binding.chipCraft.setOnClickListener   { filterByCategory("Craft") }
        binding.chipTextile.setOnClickListener { filterByCategory("Textile") }
        binding.chipOther.setOnClickListener   { filterByCategory("Other") }
        binding.chipAll.isChecked = true
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterProducts() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun filterByCategory(category: String) {
        selectedCategory = category
        filterProducts()
    }

    private fun filterProducts() {
        val query = binding.etSearch.text.toString().trim()
        val filtered = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
            val matchesSearch = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        adapter.updateList(filtered)
        binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadProducts() {
        binding.progressBar.visibility = View.VISIBLE
        db.collection(Constants.COLLECTION_PRODUCTS)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                binding.progressBar.visibility = View.GONE
                if (error != null) {
                    Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                allProducts.clear()
                snapshot?.documents?.forEach { doc ->
                    val product = doc.toObject(Product::class.java)?.copy(productId = doc.id)
                    product?.let { allProducts.add(it) }
                }
                filterProducts()
            }
    }

    override fun onResume() {
        super.onResume()
        // Products auto-refresh via snapshot listener — nothing needed here
    }
}