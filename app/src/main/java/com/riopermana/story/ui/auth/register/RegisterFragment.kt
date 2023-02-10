package com.riopermana.story.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentRegisterBinding
import com.riopermana.story.model.User
import com.riopermana.story.ui.auth.BaseAuthFragment

class RegisterFragment : BaseAuthFragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupListener()
        subscribeObserver()
        return binding.root
    }

    private fun subscribeObserver() {
        viewModel.registerResponse.observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.requestUpdateUIState()
                val action = RegisterFragmentDirections.actionRegisterFragmentToAuthDashboardFragment(user)
                findNavController().navigate(action)
            }
        }
    }

    private fun setupListener() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.emailTextField.text.toString()
            val password = binding.passwordTextField.text.toString()
            val name = binding.nameTextField.text.toString()
            val isEmailValid = !binding.emailFieldLayout.isErrorEnabled && email.isNotEmpty()
            val isPasswordValid =
                !binding.passwordFieldLayout.isErrorEnabled && password.isNotEmpty()

            if (name.isNotEmpty() && isEmailValid && isPasswordValid) {
                viewModel.createAccount(User(name, email, password))
            }
        }
    }

}