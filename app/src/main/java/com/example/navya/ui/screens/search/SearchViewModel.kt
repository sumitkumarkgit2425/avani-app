package com.example.navya.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.repository.NavyaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class SearchUiState(
        val searchQuery: String = "",
        val allPlants: List<PlantEntity> = emptyList(),
        val searchResults: List<PlantEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: NavyaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        fetchAllPlants()
    }

    private fun fetchAllPlants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository
                    .getPlants()
                    .catch { e ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        error = e.localizedMessage ?: "Unknown error"
                                )
                    }
                    .collect { plants ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        allPlants = plants,
                                        searchResults = emptyList()
                                )
                    }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val all = _uiState.value.allPlants

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = all)
        } else {
            val filtered =
                    all.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                (it.species?.contains(query, ignoreCase = true) == true)
                    }
            _uiState.value = _uiState.value.copy(searchResults = filtered)
        }
    }
}
