package com.esrannas.capstoneproject.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.databinding.ItemFavProductBinding

class FavoriteProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onDeleteClick: (ProductUI) -> Unit
) : ListAdapter<ProductUI, FavoriteProductsAdapter.FavProductViewHolder>(
    FavProductDiffUtilCallBack()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavProductViewHolder {
        return FavProductViewHolder(
            ItemFavProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onDeleteClick
        )
    }

    override fun onBindViewHolder(holder: FavProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    class FavProductViewHolder(
        private val binding: ItemFavProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onDeleteClick: (ProductUI) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductUI) {
            with(binding) {
                tvTitle.text = product.title
                tvPrice.text = "${product.price} $"
                ratingBar.rating = ((product.rate)?.toFloat() ?: 1) as Float

                Glide.with(ivProduct).load(product.imageOne).into(ivProduct)
                if (product.saleState) {
                    tvPrice.setBackgroundResource(R.drawable.strike_through)
                    tvSalePrice.text = "${product.salePrice} $"
                } else {
                    tvSalePrice.visibility = View.GONE
                }

                root.setOnClickListener {
                    onProductClick(product.id)
                }
                ivDelete.setOnClickListener {
                    onDeleteClick(product)
                }
            }
        }
    }

    class FavProductDiffUtilCallBack : DiffUtil.ItemCallback<ProductUI>() {
        override fun areItemsTheSame(
            oldItem: ProductUI,
            newItem: ProductUI
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProductUI,
            newItem: ProductUI
        ): Boolean {
            return oldItem == newItem
        }
    }
}
