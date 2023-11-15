package com.esrannas.capstoneproject.ui.favorites

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
class FavoritesViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private var _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> get() = _favoritesState

    fun getFavorites() = viewModelScope.launch {
        _favoritesState.value = FavoritesState.Loading

        val response = productRepository.getFavorites(auth.getUserId().toString())
        _favoritesState.value = when (response) {

            is Resource.Success -> FavoritesState.SuccessState(response.data)
            is Resource.Fail -> FavoritesState.EmptyScreen(response.failMessage)
            is Resource.Error -> FavoritesState.ShowPopUp(response.errorMessage)
        }
    }

    fun deleteFromFavorites(product: ProductUI) = viewModelScope.launch {
        productRepository.deleteFromFavorites(product, auth.getUserId())
        getFavorites()
    }

    fun clearFavorites() = viewModelScope.launch {
        productRepository.clearFavorites(auth.getUserId())
    }
}

sealed interface FavoritesState {
    object Loading : FavoritesState
    data class SuccessState(val products: List<ProductUI>) : FavoritesState
    data class EmptyScreen(val failMessage: String) : FavoritesState
    data class ShowPopUp(val errorMessage: String) : FavoritesState
}