package com.example.shoppingapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    
    // Views
    private lateinit var searchEditText: TextInputEditText
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var loadingProgressBar: View
    private lateinit var emptyStateTextView: View
    private lateinit var errorTextView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText)
        recyclerView = view.findViewById(R.id.productsRecyclerView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView)
        errorTextView = view.findViewById(R.id.errorTextView)

        setupRecyclerView()
        setupSearchInput()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
        }
    }

    private fun setupSearchInput() {
        searchEditText.doAfterTextChanged { text ->
            viewModel.onSearchQueryChanged(text?.toString() ?: "")
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: SearchUiState) {
        when (state) {
            SearchUiState.Initial -> {
                showEmptyState(false)
                showError(false)
                showLoading(false)
                showResults(false)
            }
            SearchUiState.Loading -> {
                showEmptyState(false)
                showError(false)
                showLoading(true)
                showResults(false)
            }
            is SearchUiState.Success -> {
                val products = state.products
                showLoading(false)
                showError(false)
                
                if (products.isEmpty()) {
                    showEmptyState(true)
                    showResults(false)
                } else {
                    showEmptyState(false)
                    showResults(true)
                    adapter.submitList(products)
                }
            }
            is SearchUiState.Error -> {
                showLoading(false)
                showEmptyState(false)
                showResults(false)
                showError(true, state.message)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingProgressBar.isVisible = show
    }

    private fun showEmptyState(show: Boolean) {
        emptyStateTextView.isVisible = show
    }

    private fun showError(show: Boolean, message: String = "") {
        errorTextView.isVisible = show
        if (show) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showResults(show: Boolean) {
        recyclerView.isVisible = show
    }
}
