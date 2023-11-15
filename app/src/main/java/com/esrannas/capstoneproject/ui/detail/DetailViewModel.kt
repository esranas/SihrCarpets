package com.esrannas.capstoneproject.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.model.request.AddToCartRequest
import com.esrannas.capstoneproject.data.model.response.BaseResponse
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.repository.AuthRepository
import com.esrannas.capstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) :
    ViewModel() {
    private var _detailState = MutableLiveData<DetailState>()
    val detailState: LiveData<DetailState> get() = _detailState

    private var _cartState = MutableLiveData<DetailState>()
    val cartState: LiveData<DetailState> get() = _cartState


    fun getProductDetail(id: Int) = viewModelScope.launch {
        _detailState.value = DetailState.Loading
        val response = productRepository.getProductDetail(id)
        _detailState.value = when (response) {

            is Resource.Success -> DetailState.SuccessState(response.data)
            is Resource.Fail -> DetailState.EmptyScreen(response.failMessage)
            is Resource.Error -> DetailState.ShowPopUp(response.errorMessage)
        }
    }

    fun addToCart(addToCartRequest: AddToCartRequest, completion: ((Boolean) -> Unit)?) =
        viewModelScope.launch {
            _cartState.value = DetailState.Loading
            val response = productRepository.addToCart(addToCartRequest, completion)
            when (response) {

                is Resource.Success -> _cartState.value = DetailState.AddToCart(response.data)


                is Resource.Error -> _cartState.value = DetailState.ShowPopUp(response.errorMessage)

                else -> Unit

            }
        }
}


sealed interface DetailState {
    object Loading : DetailState
    data class SuccessState(val product: ProductUI) : DetailState
    data class EmptyScreen(val failMessage: String) : DetailState
    data class ShowPopUp(val errorMessage: String) : DetailState
    data class AddToCart(val baseResponse: BaseResponse) : DetailState
}