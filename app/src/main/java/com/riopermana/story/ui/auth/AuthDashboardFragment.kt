package com.riopermana.story.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
        binding.btnLogIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
        return binding.root
    }

}