package com.example.navya.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.navya.data.local.entity.CartItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.checkoutEvent.collect { totalAmount ->
            val activity = context as? android.app.Activity
            activity?.let {
                val checkout = com.razorpay.Checkout()
                checkout.setKeyID("rzp_test_RB8uACuI3uJG0E")

                try {
                    val options = org.json.JSONObject()
                    options.put("name", "Navya Plants")
                    options.put("description", "Cart Purchase")
                    options.put("theme.color", "#2E7D32")
                    options.put("currency", "INR")
                    options.put("amount", (totalAmount * 100).toInt())

                    checkout.open(it, options)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                                    context,
                                    "Error checking out: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("My Cart") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                )
                            }
                        }
                )
            },
            bottomBar = {
                if (uiState.items.isNotEmpty()) {
                    Surface(
                            color = Color.White,
                            shadowElevation = 12.dp,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                    text = "Checkout via Razorpay (address flow coming soon)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                            "Total",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                    )
                                    Text(
                                            "₹${uiState.totalAmount.toInt()}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                        onClick = { viewModel.checkout() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                    Text(
                                            "Checkout",
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
    ) { innerPadding ->
        if (uiState.items.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        "Your cart is empty 🍃",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.items) { item ->
                    CartItemRow(
                            item = item,
                            onIncrement = { viewModel.increment(item) },
                            onDecrement = { viewModel.decrement(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemEntity, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier =
                        Modifier.size(80.dp)
                                .background(
                                        Color.LightGray.copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
            Text(
                    "₹${item.price.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                    item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onIncrement) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
