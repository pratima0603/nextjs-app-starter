package com.example.shoppingapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.data.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.productImageView)

        fun bind(product: Product) {
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            
            val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US)
                .format(product.price)
            priceTextView.text = formattedPrice

            product.imageUrl?.let { url ->
                Glide.with(imageView)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_product)
                    .into(imageView)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
