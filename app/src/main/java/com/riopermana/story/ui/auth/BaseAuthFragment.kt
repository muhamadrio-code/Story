package com.riopermana.story.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.riopermana.story.R
import com.riopermana.story.data.local.DataStoreUtil
import com.riopermana.story.ui.auth.login.LoginResponse
import com.riopermana.story.ui.dialogs.LoadingDialog
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.utils.UiState
import com.riopermana.story.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseAuthFragment : Fragment() {

    protected val viewModel: AuthViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            EspressoIdlingResource.decrement()
            when (uiState) {
                is UiState.OnError -> {
                    showError(uiState.message)
                    hideLoading()
                }
                is UiState.OnLoading -> {
                    showLoading()
                }
                is UiState.OnPostLoading -> {
                    hideLoading()
                }
            }
        }

        viewModel.loginResponse.observe(viewLifecycleOwner) { loginResponse ->
            handleLoginResponse(loginResponse)
        }
    }

    private fun showError(messageRes: ErrorMessageRes) {
        Toast.makeText(requireContext(), messageRes.resId, Toast.LENGTH_LONG).show()
    }

    private fun handleLoginResponse(loginResponse: LoginResponse?) {
        if (loginResponse == null) {
            Toast.makeText(requireContext(), R.string.sign_in_failed, Toast.LENGTH_LONG).show()
            return
        }

        if (!loginResponse.error) {
            lifecycleScope.launch {
                EspressoIdlingResource.increment()
                DataStoreUtil.saveToken(requireContext(), loginResponse.loginResult!!.token)
                withContext(Dispatchers.Main.immediate) {
                    EspressoIdlingResource.decrement()
                    viewModel.requestUpdateUIState()
                }
            }
        } else {
            Toast.makeText(requireContext(), R.string.sign_in_failed, Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading() = loadingDialog.show()

    private fun hideLoading() = loadingDialog.dismiss()
}