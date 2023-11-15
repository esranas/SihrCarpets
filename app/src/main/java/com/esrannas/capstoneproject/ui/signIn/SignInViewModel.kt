package com.esrannas.capstoneproject.ui.signIn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esrannas.capstoneproject.common.Resource
import com.esrannas.capstoneproject.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {
    private var _signInState = MutableLiveData<SignInState>()
    val signInState: LiveData<SignInState> get() = _signInState

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        if (authRepository.isUserLoggedIn()) {
            _signInState.value = SignInState.GoToHome
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        checkUserLoggedIn()
        if (checkFields(email, password)) {
            _signInState.value = SignInState.Loading
            val response = authRepository.signIn(email, password)
            _signInState.value = when (response) {
                is Resource.Success -> SignInState.GoToHome
                is Resource.Fail -> SignInState.ShowPopUp(response.failMessage)
                is Resource.Error -> SignInState.ShowPopUp(response.errorMessage)
            }
        }
    }

    private fun checkFields(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                _signInState.value = SignInState.ShowPopUp("Please fill email")
                false
            }

            password.isEmpty() -> {
                _signInState.value = SignInState.ShowPopUp("Please fill password")
                false
            }

            password.length < 6 -> {
                _signInState.value =
                    SignInState.ShowPopUp("Password can not be shorter than 6 characters")
                false
            }

            else -> true
        }
    }

}

sealed interface SignInState {
    object Loading : SignInState
    object GoToHome : SignInState
    data class ShowPopUp(val errorMessage: String) : SignInState
}