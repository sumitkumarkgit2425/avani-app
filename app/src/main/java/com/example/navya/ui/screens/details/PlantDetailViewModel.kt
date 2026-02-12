package com.example.navya.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.repository.AuthRepository
import com.example.navya.data.repository.NavyaRepository
import com.example.navya.data.repository.ReminderRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlantDetailViewModel
@Inject
constructor(
    private val repository: NavyaRepository,
    private val reminderRepository: ReminderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _plant = MutableStateFlow<PlantEntity?>(null)
    val plant: StateFlow<PlantEntity?> = _plant.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _reminder = MutableStateFlow<ReminderEntity?>(null)
    val reminder: StateFlow<ReminderEntity?> = _reminder.asStateFlow()

    @Inject lateinit var cartRepository: com.example.navya.data.repository.CartRepository

    private val _cartQuantity = MutableStateFlow(0)
    val cartQuantity: StateFlow<Int> = _cartQuantity.asStateFlow()

    private val _isOwned = MutableStateFlow(false)
    val isOwned: StateFlow<Boolean> = _isOwned.asStateFlow()

    fun fetchPlant(plantId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                launch {
                    repository.getPlantById(plantId).collect {
                        _plant.value = it
                        _loading.value = false
                    }
                }
                launch { reminderRepository.getReminder(plantId).collect { _reminder.value = it } }

                launch {
                    cartRepository.getCartItemFlow(plantId).collect { item ->
                        _cartQuantity.value = item?.quantity ?: 0
                    }
                }

                launch {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        repository.fetchOwnedPlants(user.uid).collect { ownedList ->
                            _isOwned.value = ownedList.any { it.id == plantId }
                        }
                    }
                }
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun addToCart() {
        val currentPlant = _plant.value ?: return
        viewModelScope.launch { cartRepository.addToCart(currentPlant) }
    }

    fun incrementCart() {
        val currentPlant = _plant.value ?: return
        viewModelScope.launch { cartRepository.addToCart(currentPlant) }
    }

    fun decrementCart() {
        val currentPlant = _plant.value ?: return
        viewModelScope.launch { cartRepository.removeFromCart(currentPlant.id) }
    }

    fun toggleReminder(enabled: Boolean) {
        val currentPlant = _plant.value ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser

        viewModelScope.launch {
            if (enabled) {
                val userId = currentUser?.uid ?: "anonymous"
                val interval = currentPlant.water_interval_days ?: 7
                reminderRepository.setReminder(currentPlant.id, currentPlant.name, userId, interval)
            } else {
                reminderRepository.cancelReminder(currentPlant.id)
            }
        }
    }

    fun updateInterval(days: Int) {
        val currentPlant = _plant.value ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: "anonymous"

        viewModelScope.launch {
            reminderRepository.setReminder(currentPlant.id, currentPlant.name, userId, days)
        }
    }

    fun waterNow() {
        val currentPlant = _plant.value ?: return
        val currentReminder = _reminder.value
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: "anonymous"

        viewModelScope.launch {
            if (currentReminder != null) {
                reminderRepository.markAsWatered(currentReminder, currentPlant.name)
            } else {
                val interval = currentPlant.water_interval_days ?: 7
                reminderRepository.setReminder(currentPlant.id, currentPlant.name, userId, interval)
            }
        }
    }
}
