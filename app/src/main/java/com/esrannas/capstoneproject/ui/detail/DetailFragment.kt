package com.esrannas.capstoneproject.ui.detail


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.common.gone
import com.esrannas.capstoneproject.common.viewBinding
import com.esrannas.capstoneproject.common.visible
import com.esrannas.capstoneproject.data.model.request.AddToCartRequest
import com.esrannas.capstoneproject.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        viewModel.getProductDetail(args.id)
        observeData()

        with(binding) {
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnAddToCart.setOnClickListener {
                val addToCartRequest = AddToCartRequest(userId.toString(), args.id)
                viewModel.addToCart(addToCartRequest) { success ->
                    if (success) {
                        progressBar.gone()
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Product is added to your cart",
                            1000
                        ).show()
                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "It is already in your cart",
                            1000
                        ).show()
                        progressBar.gone()
                    }
                }
            }
        }
    }

    private fun observeData() = with(binding) {

        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                DetailState.Loading -> progressBar.visible()

                is DetailState.SuccessState -> {
                    progressBar.gone()
                    Glide.with(ivProduct).load(state.product.imageOne).into(ivProduct)
                    tvTitle.text = state.product.title
                    tvCategory.text = state.product.category
                    tvPrice.text = "${state.product.price} $"
                    tvDescription.text = state.product.description
                    ratingBar.rating = ((state.product.rate)?.toFloat() ?: 1) as Float

                    if (state.product.saleState) {
                        tvPrice.setBackgroundResource(R.drawable.strike_through)
                        tvSalePrice.text = "${state.product.salePrice} $"
                    } else {
                        tvSalePrice.visibility = View.GONE
                    }
                }

                is DetailState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    tvEmpty.text = state.failMessage
                }

                is DetailState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }

                is DetailState.AddToCart -> {
                    Snackbar.make(
                        requireView(),
                        state.baseResponse.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                DetailState.Loading -> progressBar.visible()

                is DetailState.SuccessState -> {
                    progressBar.gone()

                }

                else -> Unit
            }

        }
    }

}
