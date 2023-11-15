package com.esrannas.capstoneproject.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.common.gone
import com.esrannas.capstoneproject.common.viewBinding
import com.esrannas.capstoneproject.common.visible
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModels<HomeViewModel>()
    private val productAdapter = ProductsAdapter(onProductClick = ::onProductClick, onFavClick = ::onFavClick)
    private val saleProductAdapter = SaleProductsAdapter(onProductClick = ::onProductClick, onFavClick = ::onFavClick)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProducts()
        viewModel.getSaleProducts()

        with(binding) {
            rvLinear.adapter = productAdapter
            rvHorizontal.adapter = saleProductAdapter
        }
        observeData()
        logOut()
    }

    private fun observeData() = with(binding) {
        viewModel.homeState.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeState.Loading -> progressBar.visible()

                is HomeState.SuccessState -> {
                    progressBar.gone()
                    productAdapter.submitList(state.products)
                }

                is HomeState.EmptyScreen -> {
                    progressBar.gone()
                    tvProducts.gone()
                    tvSale.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    tvEmpty.text = state.failMessage
                }

                is HomeState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
            }
        }

        viewModel.saleState.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeState.Loading -> progressBar.visible()

                is HomeState.SuccessState -> {
                    progressBar.gone()
                    saleProductAdapter.submitList(state.products)
                }

                is HomeState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    tvEmpty.text = state.failMessage
                }

                is HomeState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
            }
        }
    }

    private fun logOut() {
        val showPopUp = PopupMenu(
            context,
            binding.ivProfile
        )

        val logOutItem = showPopUp.menu.add(Menu.NONE, 0, 0, "Log Out")
        logOutItem.setIcon(R.drawable.ic_logout)
        showPopUp.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {
                viewModel.logOut()
                findNavController().navigate(HomeFragmentDirections.homeToSignIn())
            }
            false
        }
        binding.ivProfile.setOnClickListener {
            showPopUp.show()
        }
    }

    private fun onProductClick(id: Int) {
        findNavController().navigate(HomeFragmentDirections.homeToDetail(id))
    }

    private fun onFavClick(product: ProductUI) {
        viewModel.setFavoriteState(product)
    }
}