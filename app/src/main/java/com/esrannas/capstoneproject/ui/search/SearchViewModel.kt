package com.esrannas.capstoneproject.ui.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.model.response.ProductUI
import com.esrannas.capstoneproject.data.repository.ProductRepository
import com.esrannas.capstoneproject.ui.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository, application: Application
) : BaseViewModel(application) {

    private var _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState>
    get() = _searchState

    fun getSearchProduct(query: String) {
        launch {
            _searchState.value = SearchState.Loading
            val response = productRepository.getSearchProduct(query)
            when (response) {
                is Resource.Success -> {
                    _searchState.value = SearchState.Data(response.data)
                }

                is Resource.Error -> {
                    _searchState.value = SearchState.Error(response.errorMessage)
                }
                else->Unit
            }
        }
    }
}

sealed interface SearchState {
    object Loading : SearchState
    data class Data(val products: List<ProductUI>) : SearchState
    data class Error(val errorMessage: String) : SearchState
}