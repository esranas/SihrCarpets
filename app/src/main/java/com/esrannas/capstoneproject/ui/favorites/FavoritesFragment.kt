package com.esrannas.capstoneproject.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.common.gone
import com.esrannas.capstoneproject.common.viewBinding
import com.esrannas.capstoneproject.common.visible
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.databinding.FragmentFavoritesBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private val viewModel by viewModels<FavoritesViewModel>()
    private val favoriteProductsAdapter =
        FavoriteProductsAdapter(
            onProductClick = ::onProductClick,
            onDeleteClick = ::onDeleteClick
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()

        with(binding) {
            rvFavProducts.adapter = favoriteProductsAdapter
            btnClearFav.setOnClickListener {
                viewModel.clearFavorites()
                viewModel.getFavorites()
            }
        }
        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.favoritesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesState.Loading -> progressBar.visible()

                is FavoritesState.SuccessState -> {
                    progressBar.gone()
                    favoriteProductsAdapter.submitList(state.products)
                }

                is FavoritesState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    rvFavProducts.gone()
                    tvEmpty.text = state.failMessage
                }

                is FavoritesState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
            }
        }
    }

    private fun onProductClick(id: Int) {
        findNavController().navigate(FavoritesFragmentDirections.favoritesToDetail(id))
    }

    private fun onDeleteClick(product: ProductUI) {
        viewModel.deleteFromFavorites(product)
    }
}