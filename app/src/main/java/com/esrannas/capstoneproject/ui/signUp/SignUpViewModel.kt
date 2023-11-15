package com.esrannas.capstoneproject.ui.signUp

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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _signUpState = MutableLiveData<SignUpState>()
    val signUpState: LiveData<SignUpState> get() = _signUpState

    fun signUp(email: String, password: String) = viewModelScope.launch {
        if (checkFields(email, password)) {
            _signUpState.value = SignUpState.Loading
            val response = authRepository.signUp(email, password)
            _signUpState.value = when (response) {
                is Resource.Success -> SignUpState.GoToHome
                is Resource.Fail -> SignUpState.ShowPopUp(response.failMessage)
                is Resource.Error -> SignUpState.ShowPopUp(response.errorMessage)
            }
        }
    }

    private fun checkFields(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                _signUpState.value = SignUpState.ShowPopUp("Please fill email")
                false
            }

            password.isEmpty() -> {
                _signUpState.value = SignUpState.ShowPopUp("Please fill password")
                false
            }

            password.length < 6 -> {
                _signUpState.value =
                    SignUpState.ShowPopUp("Password can not be shorter than 6 characters")
                false
            }

            else -> true
        }
    }
}

sealed interface SignUpState {
    object Loading : SignUpState
    object GoToHome : SignUpState
    data class ShowPopUp(val errorMessage: String) : SignUpState
}