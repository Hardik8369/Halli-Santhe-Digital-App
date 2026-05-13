package com.mindmatrix.hallisanthedigital.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mindmatrix.hallisanthedigital.R
import com.mindmatrix.hallisanthedigital.databinding.ItemProductBinding
import com.mindmatrix.hallisanthedigital.model.Product

class ProductAdapter(
    private var products: MutableList<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit  // ✅ Delete callback
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        with(holder.binding) {
            tvProductName.text = product.name
            tvPrice.text = "₹${product.price}"
            tvCategory.text = product.category

            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(imgProduct)

            root.setOnClickListener { onItemClick(product) }

            // ✅ Delete button click
            btnDelete.setOnClickListener { onDeleteClick(product) }
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }

    // ✅ Remove item from list immediately (for smooth UI)
    fun removeItem(product: Product) {
        val index = products.indexOfFirst { it.productId == product.productId }
        if (index != -1) {
            products.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}