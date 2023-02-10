package com.riopermana.story.ui.auth.login

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

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupListener()
        return binding.root
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
}