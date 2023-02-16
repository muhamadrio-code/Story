package com.riopermana.story.ui.stories

import android.app.AlertDialog
import android.app.Application
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
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.riopermana.story.R
import com.riopermana.story.data.local.DataStoreUtil
import com.riopermana.story.databinding.FragmentStoriesBinding
import com.riopermana.story.di.storyRepository
import com.riopermana.story.ui.adapters.StoryPagingAdapter
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.utils.UiState
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding: FragmentStoriesBinding get() = _binding!!

    private val viewModel: StoriesViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory((requireActivity().applicationContext as Application).storyRepository)
        }
    )
    private lateinit var adapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        subscribeObserver()
        setupListener()
        hideLoading()
        return binding.root
    }

    private fun setupListener() {
        binding.fabAddNewStory.setOnClickListener {
            findNavController().navigate(R.id.newStoryFragment)
        }

        binding.btnRetry.setOnClickListener {
            adapter.refresh()
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
                        .setPositiveButton(R.string.action_logout) { _, _ ->
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
                    viewModel.getStories(token)
                }
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.OnError -> {
                    hideLoading()
                    showError(uiState.message)
                }
                is UiState.OnLoading -> {
                    showError(null)
                    showRecyclerView(false)
                    showLoading()
                }
                is UiState.OnPostLoading -> {
                    hideLoading()
                    showRecyclerView(true)
                }
            }
        }
    }

    private fun showError(errorMessageRes: ErrorMessageRes?) {
        binding.apply {
            tvMessage.isVisible = errorMessageRes != null
            errorMessageRes?.let {
                tvMessage.setText(errorMessageRes.resId)
            }
            btnRetry.isVisible = errorMessageRes != null
        }
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun showRecyclerView(isVisible: Boolean) {
        binding.storyRecyclerView.isVisible = isVisible
    }

    private fun setupRecyclerView() {
        adapter = StoryPagingAdapter()
        adapter.addLoadStateListener { combinedLoadStates ->
            val state = combinedLoadStates.mediator ?: return@addLoadStateListener
            val stateRefresh = state.refresh
            if (stateRefresh is LoadState.Loading) {
                viewModel.requestLoading()
            }

            if (state.append is LoadState.Loading) {
                viewModel.requestNotLoading()
            }

            if (stateRefresh is LoadState.Error){
                when(stateRefresh.error) {
                    is UnknownHostException -> viewModel.requestErrorState(ErrorMessageRes(R.string.connection_failed))
                    else -> viewModel.requestErrorState(ErrorMessageRes(R.string.no_data))
                }

            }
        }
        val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.storyRecyclerView.adapter = adapter
        binding.storyRecyclerView.addItemDecoration(decorator)
        adapter.setOnItemClickListener {
            val action = StoriesFragmentDirections.actionStoriesFragmentToStoryDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}