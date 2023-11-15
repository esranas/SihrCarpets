package com.esrannas.capstoneproject.ui.signIn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.common.gone
import com.esrannas.capstoneproject.common.viewBinding
import com.esrannas.capstoneproject.common.visible
import com.esrannas.capstoneproject.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private val binding by viewBinding(FragmentSignInBinding::bind)
    private val viewModel by viewModels<SignInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            btnSignIn.setOnClickListener{
                viewModel.signIn(
                email=etEmail.text.toString() ,
                password = etPassword.text.toString()
                )
            }
            signInToSignUp.setOnClickListener {
                findNavController().navigate(R.id.signUpFragment)
            }
        }
        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SignInState.Loading -> progressBar.visible()

                is SignInState.GoToHome -> {
                    progressBar.gone()
                    findNavController().navigate(R.id.signInToHome)
                }

                is SignInState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
            }
        }
    }
}



