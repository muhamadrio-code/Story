package com.riopermana.story.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentLoginBinding
import com.riopermana.story.ui.auth.BaseAuthFragment
import com.riopermana.story.utils.showToast

class LoginFragment : BaseAuthFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        playAnimation()
        setupListener()
        return binding.root
    }

    private fun playAnimation() {
        val loginBtn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(400)
        val email =
            ObjectAnimator.ofFloat(binding.emailFieldLayout, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.passwordFieldLayout, View.ALPHA, 1f).setDuration(350)

        AnimatorSet().apply {
            playTogether(email, password, loginBtn)
            start()
        }
    }

    private fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val isEmailValid = !binding.emailFieldLayout.isErrorEnabled && email.isNotEmpty()
            val isPasswordValid =
                !binding.passwordFieldLayout.isErrorEnabled && password.isNotEmpty()

            if (isEmailValid && isPasswordValid) {
                viewModel.login(email, password)
            } else {
                requireContext().showToast(R.string.sign_in_warn)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}