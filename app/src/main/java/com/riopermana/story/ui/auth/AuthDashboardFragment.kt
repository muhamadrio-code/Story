package com.riopermana.story.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentAuthDashboardBinding
import com.riopermana.story.model.User
import com.riopermana.story.ui.auth.BaseAuthFragment.Companion.PARCEL_USER

class AuthDashboardFragment : BaseAuthFragment() {

    private lateinit var binding: FragmentAuthDashboardBinding
    private val userArgs : AuthDashboardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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