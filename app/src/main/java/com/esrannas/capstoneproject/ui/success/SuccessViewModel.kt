package com.esrannas.capstoneproject.ui.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.model.request.ClearCartRequest
import com.esrannas.capstoneproject.data.model.response.BaseResponse
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.repository.AuthRepository
import com.esrannas.capstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuccessViewModel @Inject constructor(
    private val productsRepository: ProductRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private var _successState = MutableLiveData<SuccessState>()
    val successState: LiveData<SuccessState>
        get() = _successState

    private val _totalPriceAmount = MutableLiveData(0.0)
    val totalPriceAmount: LiveData<Double> = _totalPriceAmount



    private fun getCartProducts(userId: String) = viewModelScope.launch {
        val response = productsRepository.getCartProducts(userId)

        when (response) {
            is Resource.Success -> {
                _successState.value = SuccessState.Success(response.data)
                _totalPriceAmount.value = response.data.sumOf {
                    it.price ?: 0.0
                }
            }

            is Resource.Error -> {
                _successState.value = SuccessState.Error(response.errorMessage)
                resetTotalAmount()
            }
           else->Unit
        }
    }


    fun clearCart(userId: String) = viewModelScope.launch {

        val clearCartRequest = ClearCartRequest(userId)
        val response = productsRepository.clearCart(clearCartRequest)
        when (response) {
            is Resource.Success -> {
                _successState.value = SuccessState.ClearCart(response.data)
                getCartProducts(auth.getUserId())
            }

            is Resource.Error -> {
                _successState.value = SuccessState.Error(response.errorMessage)
            }
            else->Unit
        }
    }


    private fun resetTotalAmount() {
        _totalPriceAmount.value = 0.0
    }

}

sealed interface SuccessState {
    data class Success(val products: List<ProductUI>) : SuccessState
    data class Error(val errorMessage: String) : SuccessState
    data class ClearCart(val baseResponse: BaseResponse) : SuccessState
}