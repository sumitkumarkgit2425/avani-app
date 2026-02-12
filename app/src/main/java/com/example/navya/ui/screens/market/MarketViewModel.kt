package com.example.navya.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.repository.NavyaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MarketUiState(
        val plants: List<PlantEntity> = emptyList(),
        val filteredPlants: List<PlantEntity> = emptyList(),
        val categories: List<String> = listOf("Indoor", "Outdoor"),
        val searchQuery: String = "",
        val selectedCategory: String = "Indoor",
        val isLoading: Boolean = false,
        val error: String? = null,
        val message: String? = null,
        val cartDrawerPlant: PlantEntity? = null,
        val cartQuantity: Int = 1,
        val cartItems: Map<String, Int> = emptyMap()
)

@HiltViewModel
class MarketViewModel
@Inject
constructor(
        private val repository: NavyaRepository,
        private val reminderRepository: com.example.navya.data.repository.ReminderRepository,
        private val paymentRepository: com.example.navya.data.repository.PaymentRepository,
        private val authRepository: com.example.navya.data.repository.AuthRepository,
        private val cartRepository: com.example.navya.data.repository.CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    private val _paymentEvent = MutableSharedFlow<PlantEntity>()
    val paymentEvent = _paymentEvent.asSharedFlow()

    init {
        fetchAllPlants()
        observePaymentResults()
        observeCartItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.allCartItems.collect { items ->
                val cartMap = items.associate { it.plantId to it.quantity }
                _uiState.value = _uiState.value.copy(cartItems = cartMap)
            }
        }
    }

    private fun observePaymentResults() {
        viewModelScope.launch {
            paymentRepository.paymentResult.collect { result ->
                when (result) {
                    is com.example.navya.data.repository.PaymentResult.Success -> {
                        pendingPurchasePlant?.let { plant -> finalizePurchase(plant) }
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                    is com.example.navya.data.repository.PaymentResult.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                    else -> {}
                }
            }
        }
    }

    private var pendingPurchasePlant: PlantEntity? = null

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
                        val dynamicCategories =
                                plants.map { it.category.trim() }.distinct().sorted()

                        val finalCategories =
                                if (dynamicCategories.isNotEmpty()) dynamicCategories
                                else listOf("Indoor", "Outdoor")

                        val currentSelection = _uiState.value.selectedCategory
                        val newSelection =
                                if (finalCategories.contains(currentSelection)) currentSelection
                                else finalCategories.firstOrNull() ?: "Indoor"

                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        plants = plants,
                                        categories = finalCategories,
                                        selectedCategory = newSelection
                                )
                        applyFilters()
                    }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val query = state.searchQuery
        val category = state.selectedCategory
        val allPlants = state.plants

        val filtered =
                allPlants.filter { plant ->
                    val matchesCategory = plant.category.trim().equals(category, ignoreCase = true)

                    val matchesSearch =
                            if (query.isBlank()) true
                            else {
                                plant.name.contains(query, ignoreCase = true) ||
                                        (plant.species?.contains(query, ignoreCase = true) == true)
                            }
                    matchesCategory && matchesSearch
                }

        _uiState.value = state.copy(filteredPlants = filtered)
    }

    fun initiatePayment(plant: PlantEntity) {
        pendingPurchasePlant = plant
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch { _paymentEvent.emit(plant) }
    }

    private fun finalizePurchase(plant: PlantEntity) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUser()?.uid ?: "anonymous"
                val interval = plant.water_interval_days ?: 7

                repository.saveOwnedPlant(userId, plant.id)

                reminderRepository.setReminder(
                        plantId = plant.id,
                        plantName = plant.name,
                        userId = userId,
                        intervalDays = interval
                )

                cartRepository.removeFromCart(plant.id)

                _uiState.value =
                        _uiState.value.copy(message = "Purchased — reminder set for ${plant.name}")
                pendingPurchasePlant = null
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(message = "Purchase failed: ${e.localizedMessage}")
            }
        }
    }

    fun openCartDrawer(plant: PlantEntity) {
        val existingQty = _uiState.value.cartItems[plant.id] ?: 1
        _uiState.value = _uiState.value.copy(cartDrawerPlant = plant, cartQuantity = existingQty)
    }

    fun closeCartDrawer() {
        _uiState.value = _uiState.value.copy(cartDrawerPlant = null)
    }

    fun updateCartQuantity(delta: Int) {
        val currentQty = _uiState.value.cartQuantity
        val newQty = (currentQty + delta).coerceAtLeast(1)
        _uiState.value = _uiState.value.copy(cartQuantity = newQty)
    }

    fun confirmAddToCart() {
        val plant = _uiState.value.cartDrawerPlant
        val qty = _uiState.value.cartQuantity

        if (plant != null) {
            viewModelScope.launch {
                cartRepository.addToCart(plant, qty)
                _uiState.value =
                        _uiState.value.copy(
                                message = "${plant.name} updated in cart",
                                cartDrawerPlant = null
                        )
            }
        }
    }

    fun incrementCartItem(plant: PlantEntity) {
        val currentQty = _uiState.value.cartItems[plant.id] ?: 0
        viewModelScope.launch { cartRepository.addToCart(plant, currentQty + 1) }
    }

    fun decrementCartItem(plant: PlantEntity) {
        val currentQty = _uiState.value.cartItems[plant.id] ?: 0
        if (currentQty > 0) {
            viewModelScope.launch {
                cartRepository.saveCartItem(
                        com.example.navya.data.local.entity.CartItemEntity(
                                plantId = plant.id,
                                name = plant.name,
                                price = plant.price ?: 0.0,
                                imageUrl = plant.image_url ?: "",
                                quantity = currentQty - 1
                        )
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
