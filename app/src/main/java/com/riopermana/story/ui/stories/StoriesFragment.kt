package com.riopermana.story.ui.stories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.riopermana.story.R
import com.riopermana.story.data.local.DataStoreUtil
import com.riopermana.story.databinding.FragmentStoriesBinding
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.utils.UiState
import kotlinx.coroutines.launch


class StoriesFragment : Fragment() {

    private lateinit var binding: FragmentStoriesBinding
    private val viewModel: StoriesViewModel by viewModels()
    private lateinit var adapter: StoryRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoriesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        subscribeObserver()
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.edAddDescription.setOnClickListener {
            findNavController().navigate(R.id.newStoryFragment)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.action_logout -> {
                    val confirmationDialog = AlertDialog.Builder(requireContext())
                        .setTitle(R.string.action_logout)
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(R.string.action_logout){ _, _ ->
                            lifecycleScope.launch {
                                DataStoreUtil.clearSession(requireContext())
                            }
                        }
                        .create()

                    confirmationDialog.show()

                    true
                }
                else -> false
            }
        }
    }

    private fun subscribeObserver() {
        lifecycleScope.launchWhenCreated {
            DataStoreUtil.getCurrentSession(requireContext()) { token ->
                token?.let {
//                    viewModel.getStories(token)
                }
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {uiState ->
            when(uiState) {
                is UiState.OnError -> {
                    hideLoading()
                    showError(uiState.message)
                }
                is UiState.OnLoading -> {
                    showError(null)
                    showLoading()
                }
                is UiState.OnPostLoading -> {
                    hideLoading()
                }
            }
        }
    }

    private fun showError(errorMessageRes: ErrorMessageRes?) {
        binding.tvMessage.isVisible = errorMessageRes != null
        errorMessageRes?.let {
            binding.tvMessage.setText(errorMessageRes.resId)
        }
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = StoryRecyclerViewAdapter()
        binding.storyRecyclerView.adapter = adapter
    }

}