package com.example.navya

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.CartItemEntity
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.repository.CartRepository
import com.example.navya.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class MainUiState(
        val isCartOpen: Boolean = false,
        val cartItems: List<CartItemEntity> = emptyList(),
        val totalAmount: Double = 0.0,
        val isDarkTheme: Boolean? = null,
        val userName: String? = null
)

@HiltViewModel
class MainViewModel
@Inject
constructor(
        private val cartRepository: CartRepository,
        private val paymentRepository: PaymentRepository,
        private val reminderRepository: com.example.navya.data.repository.ReminderRepository,
        private val navyaRepository: com.example.navya.data.repository.NavyaRepository,
        private val authRepository: com.example.navya.data.repository.AuthRepository,
        private val userRepository: com.example.navya.data.repository.UserRepository,
        private val settingsRepository: com.example.navya.data.repository.SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _uiEvent = kotlinx.coroutines.flow.MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            launch {
                settingsRepository.isDarkTheme.collect { isDark ->
                    _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
                }
            }

            authRepository.getCurrentUser()?.let { user ->
                _uiState.value = _uiState.value.copy(userName = user.displayName)
                launch { userRepository.syncUser(user) }
                launch { reminderRepository.syncReminders(user.uid) }
            }

            cartRepository.allCartItems.collect { items ->
                val total = items.sumOf { it.price * it.quantity }
                _uiState.value = _uiState.value.copy(cartItems = items, totalAmount = total)
            }
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkTheme(isDark) }
    }

    fun toggleCart() {
        _uiState.value = _uiState.value.copy(isCartOpen = !_uiState.value.isCartOpen)
    }

    fun openCart() {
        _uiState.value = _uiState.value.copy(isCartOpen = true)
    }

    fun closeCart() {
        _uiState.value = _uiState.value.copy(isCartOpen = false)
    }

    fun increment(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.addToCart(
                    PlantEntity(
                            id = item.plantId,
                            name = item.name,
                            price = item.price,
                            image_url = item.imageUrl,
                            species = null,
                            min_lux = null,
                            max_lux = null,
                            water_interval_days = null,
                            description = null,
                            currency = "INR",
                            soil = null,
                            difficulty = null
                    ),
                    item.quantity + 1
            )
        }
    }

    fun decrement(item: CartItemEntity) {
        if (item.quantity > 0) {
            viewModelScope.launch {
                cartRepository.saveCartItem(item.copy(quantity = item.quantity - 1))
            }
        }
    }

    fun incrementCartItem(plant: PlantEntity) {

        val currentItem = _uiState.value.cartItems.find { it.plantId == plant.id }
        val currentQty = currentItem?.quantity ?: 0
        viewModelScope.launch { cartRepository.addToCart(plant, currentQty + 1) }
    }

    fun decrementCartItem(plant: PlantEntity) {
        val currentItem = _uiState.value.cartItems.find { it.plantId == plant.id }
        if (currentItem != null && currentItem.quantity > 0) {
            viewModelScope.launch {
                if (currentItem.quantity == 1) {
                    cartRepository.removeFromCart(plant.id)
                } else {
                    cartRepository.saveCartItem(
                            currentItem.copy(quantity = currentItem.quantity - 1)
                    )
                }
            }
        }
    }

    fun handlePaymentSuccess(orderId: String) {
        viewModelScope.launch {
            val items = _uiState.value.cartItems
            val user = authRepository.getCurrentUser()
            val userId = user?.uid ?: "anonymous"

            if (user != null) {
                userRepository.syncUser(user)
            }

            items.forEach { cartItem ->
                launch { navyaRepository.saveOwnedPlant(userId, cartItem.plantId) }

                try {
                    val plant = navyaRepository.getPlantById(cartItem.plantId).firstOrNull()
                    val interval = plant?.water_interval_days ?: 7

                    reminderRepository.setReminder(
                            plantId = cartItem.plantId,
                            plantName = cartItem.name,
                            userId = userId,
                            intervalDays = interval
                    )
                } catch (e: Exception) {

                    reminderRepository.setReminder(
                            plantId = cartItem.plantId,
                            plantName = cartItem.name,
                            userId = userId,
                            intervalDays = 7
                    )
                }
            }

            cartRepository.clearCart()

            paymentRepository.updatedPaymentResult(
                    com.example.navya.data.repository.PaymentResult.Success(orderId)
            )

            _uiEvent.emit(UiEvent.ShowToast("Order Placed, Saved to My Plants & Reminders Set!"))
        }
    }

    fun handlePaymentError(code: Int, response: String?) {
        viewModelScope.launch {
            val message = response ?: "Unknown error"
            paymentRepository.updatedPaymentResult(
                    com.example.navya.data.repository.PaymentResult.Error(code, message)
            )

            _uiEvent.emit(UiEvent.ShowToast("Payment Failed. Please try again."))
        }
    }

    fun getCartQuantity(plantId: String): kotlinx.coroutines.flow.Flow<Int> {
        return _uiState.map { state ->
            state.cartItems.find { it.plantId == plantId }?.quantity ?: 0
        }
    }
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
