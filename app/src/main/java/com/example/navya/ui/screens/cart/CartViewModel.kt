package com.example.navya.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.CartItemEntity
import com.example.navya.data.repository.CartRepository
import com.example.navya.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartUiState(
        val items: List<CartItemEntity> = emptyList(),
        val totalAmount: Double = 0.0,
        val isLoading: Boolean = false,
)

@HiltViewModel
class CartViewModel
@Inject
constructor(
        private val cartRepository: CartRepository,
        private val paymentRepository: PaymentRepository,
        private val authRepository: com.example.navya.data.repository.AuthRepository,
        private val navyaRepository: com.example.navya.data.repository.NavyaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _checkoutEvent = MutableSharedFlow<Double>()
    val checkoutEvent = _checkoutEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            cartRepository.allCartItems.collect { items ->
                val total = items.sumOf { it.price * it.quantity }
                _uiState.value = _uiState.value.copy(items = items, totalAmount = total)
            }
        }
        observePaymentResults()
    }

    private fun observePaymentResults() {
        viewModelScope.launch {
            paymentRepository.paymentResult.collect { result ->
                if (result is com.example.navya.data.repository.PaymentResult.Success) {
                    finalizeCartPurchase()
                } else if (result is com.example.navya.data.repository.PaymentResult.Error) {}
            }
        }
    }

    fun checkout() {
        val total = _uiState.value.totalAmount
        if (total > 0) {
            viewModelScope.launch { _checkoutEvent.emit(total) }
        } else {}
    }

    private suspend fun finalizeCartPurchase() {
        val userId = authRepository.getCurrentUser()?.uid ?: "anonymous"
        val items = _uiState.value.items

        items.forEach { item -> navyaRepository.saveOwnedPlant(userId, item.plantId) }
        cartRepository.clearCart()
    }

    fun increment(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item.plantId, item.quantity + 1)
            cartRepository.addToCart(
                    com.example.navya.data.local.entity.PlantEntity(
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
}
