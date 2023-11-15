package com.esrannas.capstoneproject.ui.cart


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
import com.esrannas.capstoneproject.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {
    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel by viewModels<CartViewModel>()
    private val cartProductsAdapter = CartProductsAdapter(
        onProductClick = ::onProductClick,
        onCartDeleteClick = ::onCartDeleteClick
    )

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid.toString()

        viewModel.getCartProducts(userId.toString())
        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.tvPrice.text = getString(R.string.total_price_format, totalPrice)
        }

        with(binding) {
            rvCartProducts.adapter = cartProductsAdapter
            btnClearCart.setOnClickListener {
                viewModel.clearCart(userId)
                viewModel.getCartProducts(userId)
            }
            btnBuy.setOnClickListener {
                if (cartProductsAdapter.currentList.isNotEmpty()) {
                    findNavController().navigate(CartFragmentDirections.cartToPayment())
                } else {
                    Snackbar.make(
                        requireView(),
                        "Your cart is empty. Add items before proceeding.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                CartState.Loading -> progressBar.visible()

                is CartState.SuccessState -> {
                    progressBar.gone()
                    cartProductsAdapter.submitList(state.products)
                }

                is CartState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    tvEmpty.text = state.failMessage
                    val list = ArrayList<ProductUI>()
                    cartProductsAdapter.submitList(list)
                    viewModel.setTotalPrice(0.0)
                }

                is CartState.DeleteFromCart -> {
                    Snackbar.make(requireView(), state.baseResponse.message.toString(), 1000).show()
                }

                is CartState.ClearCart -> {
                    Snackbar.make(requireView(), state.baseResponse.message.toString(), 1000).show()
                }

                is CartState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
            }
        }
    }

    private fun onProductClick(id: Int) {
        findNavController().navigate(CartFragmentDirections.cartToDetail(id))
    }

    private fun onCartDeleteClick(id: Int, userId: String) {
        viewModel.deleteFromCart(id, userId)
        viewModel.getCartProducts(userId)
    }
}
