package com.example.navya.ui.screens.market

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navya.ui.components.MarketPlantCard

@OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterial3Api::class,
        androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun MarketScreen(navController: NavController, viewModel: MarketViewModel = hiltViewModel()) {
        val uiState by viewModel.uiState.collectAsState()
        val context = androidx.compose.ui.platform.LocalContext.current
        val activity = context as? android.app.Activity

        LaunchedEffect(Unit) {
                viewModel.paymentEvent.collect { plant ->
                        activity?.let {
                                val checkout = com.razorpay.Checkout()
                                checkout.setKeyID(com.example.navya.BuildConfig.RAZORPAY_KEY_ID)
                                checkout.setImage(com.example.navya.R.drawable.avanipng)

                                try {
                                        val options = org.json.JSONObject()
                                        options.put("name", "Navya Plants")
                                        options.put("description", "Purchase ${plant.name}")
                                        options.put("theme.color", "#2E7D32")
                                        options.put("currency", plant.currency ?: "INR")

                                        val price = plant.price ?: 100.0
                                        options.put("amount", (price * 100).toInt())

                                        checkout.open(it, options)
                                } catch (e: Exception) {
                                        android.widget.Toast.makeText(
                                                        it,
                                                        "Error in payment: ${e.message}",
                                                        android.widget.Toast.LENGTH_SHORT
                                                )
                                                .show()
                                }
                        }
                }
        }

        androidx.activity.compose.BackHandler {
                navController.navigate("home_screen") {
                        popUpTo("home_screen") { inclusive = true }
                        launchSingleTop = true
                }
        }

        Scaffold(
                contentWindowInsets = WindowInsets(0.dp),
        ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 16.dp
                                                        )
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(bottom = 16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        "Market",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineMedium,
                                                        fontWeight = FontWeight.Bold
                                                )

                                                val mainViewModel: com.example.navya.MainViewModel =
                                                        if (activity is
                                                                        androidx.activity.ComponentActivity
                                                        ) {
                                                                hiltViewModel(activity)
                                                        } else {
                                                                hiltViewModel()
                                                        }
                                                val cartState by
                                                        mainViewModel.uiState.collectAsState()
                                                val cartCount =
                                                        cartState.cartItems.sumOf { it.quantity }

                                                IconButton(onClick = { mainViewModel.openCart() }) {
                                                        BadgedBox(
                                                                badge = {
                                                                        if (cartCount > 0)
                                                                                Badge {
                                                                                        Text(
                                                                                                cartCount
                                                                                                        .toString()
                                                                                        )
                                                                                }
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.ShoppingCart,
                                                                        contentDescription = "Cart"
                                                                )
                                                        }
                                                }
                                        }

                                        com.example.navya.ui.components.NavyaSearchBar(
                                                query = uiState.searchQuery,
                                                onQueryChange = {
                                                        viewModel.onSearchQueryChanged(it)
                                                },
                                                placeholder = "Search plants...",
                                                modifier = Modifier
                                        )
                                }

                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .weight(1f)
                                                        .padding(horizontal = 16.dp)
                                                        .background(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                                                .copy(alpha = 0.3f),
                                                                shape =
                                                                        RoundedCornerShape(
                                                                                topStart = 24.dp,
                                                                                topEnd = 24.dp
                                                                        )
                                                        )
                                                        .padding(16.dp)
                                ) {
                                        AnimatedContent(
                                                targetState = uiState.selectedCategory,
                                                transitionSpec = {
                                                        val categories = listOf("Indoor", "Outdoor")
                                                        val initialIndex =
                                                                categories.indexOf(initialState)
                                                        val targetIndex =
                                                                categories.indexOf(targetState)

                                                        if (initialState == targetState) {
                                                                EnterTransition.None with
                                                                        ExitTransition.None
                                                        } else if (targetIndex > initialIndex) {
                                                                slideInHorizontally { width ->
                                                                        width
                                                                } + fadeIn() with
                                                                        slideOutHorizontally { width
                                                                                ->
                                                                                -width
                                                                        } + fadeOut()
                                                        } else {
                                                                slideInHorizontally { width ->
                                                                        -width
                                                                } + fadeIn() with
                                                                        slideOutHorizontally { width
                                                                                ->
                                                                                width
                                                                        } + fadeOut()
                                                        }
                                                },
                                                label = "market_grid_transition",
                                                modifier = Modifier.fillMaxSize()
                                        ) { targetCategory ->
                                                val categoryPlants =
                                                        remember(
                                                                uiState.plants,
                                                                targetCategory,
                                                                uiState.searchQuery
                                                        ) {
                                                                uiState.plants.filter {
                                                                        it.category
                                                                                .trim()
                                                                                .equals(
                                                                                        targetCategory,
                                                                                        ignoreCase =
                                                                                                true
                                                                                ) &&
                                                                                (uiState.searchQuery
                                                                                        .isBlank() ||
                                                                                        it.name
                                                                                                .contains(
                                                                                                        uiState.searchQuery,
                                                                                                        ignoreCase =
                                                                                                                true
                                                                                                ) ||
                                                                                        it.species
                                                                                                ?.contains(
                                                                                                        uiState.searchQuery,
                                                                                                        ignoreCase =
                                                                                                                true
                                                                                                ) ==
                                                                                                true)
                                                                }
                                                        }

                                                LazyVerticalGrid(
                                                        columns = GridCells.Fixed(2),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(8.dp),
                                                        contentPadding =
                                                                PaddingValues(bottom = 16.dp),
                                                        modifier = Modifier.fillMaxSize()
                                                ) {
                                                        item(span = { GridItemSpan(2) }) {
                                                                MarketTabRow(
                                                                        selectedCategory =
                                                                                targetCategory,
                                                                        onCategorySelected = {
                                                                                viewModel
                                                                                        .onCategorySelected(
                                                                                                it
                                                                                        )
                                                                        },
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        bottom =
                                                                                                16.dp
                                                                                )
                                                                )
                                                        }

                                                        items(
                                                                items = categoryPlants,
                                                                key = { it.id }
                                                        ) { plant ->
                                                                val qty =
                                                                        uiState.cartItems[plant.id]
                                                                                ?: 0

                                                                MarketPlantCard(
                                                                        plant = plant,
                                                                        quantity = qty,
                                                                        onAddToCart = {
                                                                                viewModel
                                                                                        .incrementCartItem(
                                                                                                plant
                                                                                        )
                                                                        },
                                                                        onIncrement = {
                                                                                viewModel
                                                                                        .incrementCartItem(
                                                                                                plant
                                                                                        )
                                                                        },
                                                                        onDecrement = {
                                                                                viewModel
                                                                                        .decrementCartItem(
                                                                                                plant
                                                                                        )
                                                                        },
                                                                        onClick = { selectedPlant ->
                                                                                navController
                                                                                        .navigate(
                                                                                                "plant_detail/${selectedPlant.id}"
                                                                                        )
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        if (uiState.isLoading) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                MaterialTheme.colorScheme.background
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                        }
                }
        }
}

@Composable
fun MarketTabRow(
        selectedCategory: String,
        onCategorySelected: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val categories = listOf("Indoor", "Outdoor")
        val selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0)

        Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp),
                modifier =
                        modifier.fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val tabWidth = maxWidth / categories.size

                        val indicatorOffset by
                                animateDpAsState(
                                        targetValue = tabWidth * selectedIndex,
                                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                                        label = "indicator"
                                )

                        Box(
                                modifier =
                                        Modifier.offset(x = indicatorOffset)
                                                .width(tabWidth)
                                                .fillMaxHeight()
                                                .padding(4.dp)
                                                .background(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = RoundedCornerShape(20.dp)
                                                )
                        )

                        Row(modifier = Modifier.fillMaxSize()) {
                                categories.forEachIndexed { index, category ->
                                        val isSelected = index == selectedIndex
                                        val textColor by
                                                animateColorAsState(
                                                        targetValue =
                                                                if (isSelected)
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                                else
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant,
                                                        label = "text_color"
                                                )

                                        val icon =
                                                if (category == "Indoor") Icons.Filled.Home
                                                else Icons.Filled.WbSunny

                                        Box(
                                                modifier =
                                                        Modifier.weight(1f)
                                                                .fillMaxHeight()
                                                                .clickable(
                                                                        interactionSource =
                                                                                remember {
                                                                                        MutableInteractionSource()
                                                                                },
                                                                        indication = null
                                                                ) { onCategorySelected(category) },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {
                                                        Icon(
                                                                imageVector = icon,
                                                                contentDescription = null,
                                                                tint = textColor,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Text(
                                                                text = category,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = textColor
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
