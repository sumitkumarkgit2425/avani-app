package com.example.navya.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.repository.AuthRepository
import com.example.navya.data.repository.NavyaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlantsViewModel @Inject constructor(
    private val navyaRepository: NavyaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _ownedPlants = MutableStateFlow<List<PlantEntity>>(emptyList())
    val ownedPlants: StateFlow<List<PlantEntity>> = _ownedPlants.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadOwnedPlants()
    }

    private fun loadOwnedPlants() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            if (user != null) {
                navyaRepository.fetchOwnedPlants(user.uid).collect { plants ->
                    _ownedPlants.value = plants
                    _isLoading.value = false
                }
            } else {
                _isLoading.value = false
                _ownedPlants.value = emptyList()
            }
        }
    }
}
