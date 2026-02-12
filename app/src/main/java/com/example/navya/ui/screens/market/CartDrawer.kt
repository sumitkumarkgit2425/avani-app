package com.example.navya.ui.screens.market

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.navya.data.local.entity.CartItemEntity

@Composable
fun CartDrawer(
        isOpen: Boolean,
        cartItems: List<com.example.navya.data.local.entity.CartItemEntity>,
        totalAmount: Double,
        onClose: () -> Unit,
        onIncrement: (com.example.navya.data.local.entity.CartItemEntity) -> Unit,
        onDecrement: (com.example.navya.data.local.entity.CartItemEntity) -> Unit,
        onCheckout: () -> Unit,
        onItemClick: (String) -> Unit
) {
    if (!isOpen) return

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { onClose() }
        )

        AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Column(
                    modifier =
                            Modifier.fillMaxHeight()
                                    .fillMaxWidth(0.85f)
                                    .windowInsetsPadding(WindowInsets.statusBars)
                                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(24.dp)
            ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "My Cart 🛒",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (cartItems.isEmpty()) {
                    Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                "Your cart is empty",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {

                    LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemRow(
                                    item = item,
                                    onIncrement = onIncrement,
                                    onDecrement = onDecrement,
                                    onItemClick = { onItemClick(item.plantId) }
                            )
                        }
                    }

                    Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                            text = "Checkout via Razorpay (address flow coming soon)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                    Modifier.padding(bottom = 8.dp)
                                            .align(Alignment.CenterHorizontally)
                    )

                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                "Total",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                "₹${totalAmount.toInt()}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                            onClick = onCheckout,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                    ),
                            shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                                "Checkout",
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
        item: com.example.navya.data.local.entity.CartItemEntity,
        onIncrement: (com.example.navya.data.local.entity.CartItemEntity) -> Unit,
        onDecrement: (com.example.navya.data.local.entity.CartItemEntity) -> Unit,
        onItemClick: () -> Unit
) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                            )
                            .clickable { onItemClick() }
                            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier =
                        Modifier.size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                    "₹${item.price.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onDecrement(item) }, modifier = Modifier.size(32.dp)) {
                Icon(
                        Icons.Default.Remove,
                        "Remove",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                    item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = { onIncrement(item) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
