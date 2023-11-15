package com.esrannas.capstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.repository.AuthRepository
import com.esrannas.capstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private var _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState

    private val _saleState = MutableLiveData<HomeState>()
    val saleState: LiveData<HomeState> get() = _saleState


    fun getProducts() = viewModelScope.launch {
        _homeState.value = HomeState.Loading
        val response = productRepository.getProducts()
        _homeState.value = when (response) {

            is Resource.Success -> HomeState.SuccessState(response.data)
            is Resource.Fail -> HomeState.EmptyScreen(response.failMessage)
            is Resource.Error -> HomeState.ShowPopUp(response.errorMessage)
        }

    }

    fun logOut()=viewModelScope.launch {
        auth.logOut()
    }

    fun getSaleProducts() = viewModelScope.launch {
        _saleState.value = HomeState.Loading
        val response = productRepository.getProducts(saleState = true)
        _saleState.value = when (response) {

            is Resource.Success -> HomeState.SuccessState(response.data)
            is Resource.Fail -> HomeState.EmptyScreen(response.failMessage)
            is Resource.Error -> HomeState.ShowPopUp(response.errorMessage)

        }
    }

    fun setFavoriteState(product: ProductUI) = viewModelScope.launch {
        if (product.isFav) {
            productRepository.deleteFromFavorites(product, auth.getUserId())
        } else {
            productRepository.addToFavorites(product, auth.getUserId())
        }
        getProducts()
        getSaleProducts()
    }
}

sealed interface HomeState {
    object Loading : HomeState
    data class SuccessState(val products: List<ProductUI>) : HomeState
    data class EmptyScreen(val failMessage: String) : HomeState
    data class ShowPopUp(val errorMessage: String) : HomeState
}