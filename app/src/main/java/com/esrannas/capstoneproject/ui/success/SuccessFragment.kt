package com.esrannas.capstoneproject.ui.success


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esrannas.capstoneproject.MainApplication
import com.esrannas.capstoneproject.R
import com.esrannas.capstoneproject.common.viewBinding
import com.esrannas.capstoneproject.databinding.FragmentSuccessBinding
import com.esrannas.capstoneproject.di.RepositoryModule
import dagger.hilt.EntryPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessFragment : Fragment(R.layout.fragment_success) {
    private val binding by viewBinding(FragmentSuccessBinding::bind)
    private val viewModel by viewModels<SuccessViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAnim()

            binding.btnBackToHome.setOnClickListener {
                val auth = EntryPoints.get(MainApplication.context, RepositoryModule.RepositoryModuleInterface::class.java).getAuthRepo()
                val userId=auth.getUserId()
                viewModel.clearCart(userId)
                findNavController().navigate(SuccessFragmentDirections.successToHome())
            }
    }
    private fun setupAnim() {
        binding.lottieAnimationView.setAnimation(R.raw.payment_successful_animation)
        binding.lottieAnimationView.speed = 0.8f
        binding.lottieAnimationView.playAnimation()
    }
}

