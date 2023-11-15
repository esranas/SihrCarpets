package com.esrannas.capstoneproject.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.model.request.ClearCartRequest
import com.esrannas.capstoneproject.data.model.request.DeleteFromCartRequest
import com.esrannas.capstoneproject.data.model.response.BaseResponse
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.repository.AuthRepository
import com.esrannas.capstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val productRepository: ProductRepository, private val authRepository: AuthRepository
) : ViewModel() {
    private var _cartState = MutableLiveData<CartState>()
    val cartState: LiveData<CartState> get() = _cartState

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> = _totalPrice

    init {
        _totalPrice.value = 0.0
    }

    fun setTotalPrice(price: Double) {
        _totalPrice.value = price
    }

    fun getCartProducts(userId: String) = viewModelScope.launch {
        _cartState.value = CartState.Loading
        val response = productRepository.getCartProducts(userId)
        _cartState.value = when (response) {

            is Resource.Success -> {
                if (response.data.isNullOrEmpty()) {
                    _totalPrice.value = 0.0
                } else {
                    _totalPrice.value = calculateTotalPrice(response.data)
                }

                CartState.SuccessState(response.data)
            }

            is Resource.Fail -> CartState.EmptyScreen(response.failMessage)
            is Resource.Error -> CartState.ShowPopUp(response.errorMessage)
        }
    }

    fun calculateTotalPrice(products: List<ProductUI>): Double {
        return products.sumOf {
            if (it.saleState != true) it.price else it.salePrice
        }
    }

    fun deleteFromCart(id: Int, userId: String) = viewModelScope.launch {
        _cartState.value = CartState.Loading
        val deleteFromCartRequest = DeleteFromCartRequest(id, userId)
        val response = productRepository.deleteFromCart(deleteFromCartRequest)
        when (response) {
            is Resource.Success -> {
                CartState.DeleteFromCart(response.data)
                getCartProducts(authRepository.getUserId())
            }

            is Resource.Fail -> CartState.EmptyScreen(response.failMessage)
            is Resource.Error -> CartState.ShowPopUp(response.errorMessage)

        }
    }

    fun clearCart(userId: String) = viewModelScope.launch {
        val clearCartRequest = ClearCartRequest(userId)
        val response = productRepository.clearCart(clearCartRequest)
        when (response) {

            is Resource.Success -> {
                CartState.ClearCart(response.data)

                getCartProducts(authRepository.getUserId())
                _totalPrice.value = 0.0
            }

            is Resource.Fail -> CartState.EmptyScreen(response.failMessage)
            is Resource.Error -> CartState.ShowPopUp(response.errorMessage)

        }
    }
}

sealed interface CartState {
    data object Loading : CartState
    data class SuccessState(val products: List<ProductUI>) : CartState
    data class DeleteFromCart(val baseResponse: BaseResponse) : CartState
    data class ClearCart(val baseResponse: BaseResponse) : CartState
    data class EmptyScreen(val failMessage: String) : CartState
    data class ShowPopUp(val errorMessage: String) : CartState
}