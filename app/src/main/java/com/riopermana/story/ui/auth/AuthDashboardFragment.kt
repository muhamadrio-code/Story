package com.riopermana.story.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentAuthDashboardBinding

class AuthDashboardFragment : BaseAuthFragment() {

    private lateinit var binding: FragmentAuthDashboardBinding
    private val userArgs : AuthDashboardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.background_color)
        binding = FragmentAuthDashboardBinding.inflate(inflater, container, false)
        val user = userArgs.user
        user?.let {
            viewModel.login(email = it.email, password = it.password)
        }
        setupListener()
        playAnimation()
        return binding.root
    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(binding.btnLogIn, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val logo = ObjectAnimator.ofFloat(binding.ivAppLogo, View.ALPHA, 1f).setDuration(300)
        val illustration = ObjectAnimator.ofFloat(binding.ivIllustration, View.ALPHA, 1f).setDuration(500)
        val welcomeText = ObjectAnimator.ofFloat(binding.welcomeText, View.ALPHA, 1f).setDuration(500)
        val welcomeText2 = ObjectAnimator.ofFloat(binding.welcomeText2, View.ALPHA, 1f).setDuration(500)

        val together1 = AnimatorSet().apply {
            playTogether(logo, illustration, welcomeText, welcomeText2)
        }

        val sequentially = AnimatorSet().apply {
            playSequentially(register,login)
        }

        AnimatorSet().apply {
            playTogether(sequentially,together1)
            start()
        }
    }

    private fun setupListener() {
        binding.btnLogIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

}