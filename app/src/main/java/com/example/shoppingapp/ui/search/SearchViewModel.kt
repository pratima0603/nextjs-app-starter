package com.example.shoppingapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.model.Product
import com.example.shoppingapp.domain.usecase.PerformSearchUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val products: List<Product>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val performSearchUseCase: PerformSearchUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait for 300ms of inactivity
                .distinctUntilChanged()
                .onEach { query -> 
                    if (query.isNotBlank()) {
                        _uiState.value = SearchUiState.Loading
                    }
                }
                .flatMapLatest { query ->
                    flow {
                        if (query.isBlank()) {
                            emit(SearchUiState.Success(emptyList()))
                            return@flow
                        }
                        
                        performSearchUseCase(query).fold(
                            onSuccess = { products ->
                                emit(SearchUiState.Success(products))
                            },
                            onFailure = { error ->
                                emit(SearchUiState.Error(error.message ?: "Unknown error occurred"))
                            }
                        )
                    }
                }
                .catch { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown error occurred")
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
