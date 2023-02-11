package com.riopermana.story.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
        playAnimation()
        return binding.root
    }

    private fun playAnimation() {
        val registerBtn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(450)
        val name = ObjectAnimator.ofFloat(binding.nameFieldLayout, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.emailFieldLayout, View.ALPHA, 1f).setDuration(350)
        val password = ObjectAnimator.ofFloat(binding.passwordFieldLayout, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playTogether(name,email,password,registerBtn)
            start()
        }
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
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val name = binding.edRegisterName.text.toString()
            val isEmailValid = !binding.emailFieldLayout.isErrorEnabled && email.isNotEmpty()
            val isPasswordValid =
                !binding.passwordFieldLayout.isErrorEnabled && password.isNotEmpty()

            if (name.isNotEmpty() && isEmailValid && isPasswordValid) {
                viewModel.createAccount(User(name, email, password))
            }
        }
    }

}