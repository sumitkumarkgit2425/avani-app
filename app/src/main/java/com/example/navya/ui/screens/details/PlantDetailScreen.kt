package com.example.navya.ui.screens.details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.ReminderEntity

@Composable
fun PlantDetailScreen(
        navController: NavController,
        plantId: String,
        viewModel: PlantDetailViewModel = hiltViewModel()
) {
        val plant by viewModel.plant.collectAsState()
        val isLoading by viewModel.loading.collectAsState()
        val reminder by viewModel.reminder.collectAsState()
        val isOwned by viewModel.isOwned.collectAsState()

        LaunchedEffect(plantId) { viewModel.fetchPlant(plantId) }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                        plant?.let { plantData ->
                                PlantDetailContent(
                                        plant = plantData,
                                        reminder = reminder,
                                        cartQuantity =
                                                viewModel.cartQuantity.collectAsState().value,
                                        isOwned = isOwned,
                                        onBackClick = { navController.popBackStack() },
                                        onArClick = {
                                                val encodedUrl =
                                                        java.net.URLEncoder.encode(
                                                                plantData.image_url,
                                                                java.nio.charset.StandardCharsets
                                                                        .UTF_8
                                                                        .toString()
                                                        )
                                                navController.navigate(
                                                        "ar_screen?encodedImage=$encodedUrl"
                                                )
                                        },
                                        onAddToCartClick = { viewModel.addToCart() },
                                        onIncrementCart = { viewModel.incrementCart() },
                                        onDecrementCart = { viewModel.decrementCart() },
                                        onToggleReminder = { viewModel.toggleReminder(it) },
                                        onIntervalChange = { viewModel.updateInterval(it) },
                                        onWaterNow = { viewModel.waterNow() }
                                )
                        }
                                ?: run {
                                        Text(
                                                "Plant not found",
                                                modifier = Modifier.align(Alignment.Center)
                                        )
                                }
                }
        }
}

@Composable
fun PlantDetailContent(
        plant: PlantEntity,
        reminder: ReminderEntity?,
        cartQuantity: Int,
        isOwned: Boolean,
        onBackClick: () -> Unit,
        onArClick: () -> Unit,
        onAddToCartClick: () -> Unit,
        onIncrementCart: () -> Unit,
        onDecrementCart: () -> Unit,
        onToggleReminder: (Boolean) -> Unit,
        onIntervalChange: (Int) -> Unit,
        onWaterNow: () -> Unit
) {
        val scrollState = rememberScrollState()

        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(12.dp)
                                        .height(380.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                ) {
                        AsyncImage(
                                model = plant.image_url,
                                contentDescription = plant.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                        )

                        Box(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color.Transparent,
                                                                                Color.Transparent,
                                                                                Color.Transparent,
                                                                                Color.Black.copy(
                                                                                        alpha = 0.4f
                                                                                )
                                                                        ),
                                                                startY = 0f,
                                                                endY = Float.POSITIVE_INFINITY
                                                        )
                                                )
                        )

                        IconButton(
                                onClick = onBackClick,
                                modifier =
                                        Modifier.padding(top = 16.dp, start = 16.dp)
                                                .background(
                                                        MaterialTheme.colorScheme.surface,
                                                        CircleShape
                                                )
                        ) {
                                Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface
                                )
                        }

                        Column(
                                modifier =
                                        Modifier.align(Alignment.BottomStart)
                                                .padding(start = 24.dp, bottom = 24.dp)
                        ) {
                                Text(
                                        text = plant.name,
                                        style =
                                                MaterialTheme.typography.headlineMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        shadow =
                                                                androidx.compose.ui.graphics.Shadow(
                                                                        color =
                                                                                Color.Black.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                        blurRadius = 4f
                                                                )
                                                )
                                )
                             Surface(
                                        shape = RoundedCornerShape(50),
                                        color =
                                                MaterialTheme.colorScheme.surface.copy(
                                                        alpha = 0.9f
                                                ),
                                        modifier = Modifier.padding(top = 8.dp)
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 12.dp,
                                                                vertical = 6.dp
                                                        ),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Outlined.WbSunny,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = Color(0xFFFFA000)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                        text =
                                                                "${plant.min_lux}-${plant.max_lux} lux",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                )
                                        }
                                }
                        }
                }

                if (!isOwned) {
                        Card(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 24.dp)
                                                .offset(y = (-24).dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                        )
                        ) {
                                Row(
                                        modifier =
                                                Modifier.padding(
                                                                horizontal = 10.dp,
                                                                vertical = 16.dp
                                                        )
                                                        .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Column {
                                                Text(
                                                        text = "Price",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Text(
                                                        text = "₹${plant.price ?: 0}",
                                                        style =
                                                                MaterialTheme.typography.titleLarge
                                                                        .copy(
                                                                                fontSize = 20.sp,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                )
                                        }

                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f),
                                                horizontalArrangement = Arrangement.End
                                        ) {
                                                OutlinedButton(
                                                        onClick = onArClick,
                                                        shape = RoundedCornerShape(50),
                                                        contentPadding =
                                                                PaddingValues(horizontal = 12.dp),
                                                        modifier = Modifier.height(44.dp)
                                                ) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Default.ViewInAr,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                                "Try AR",
                                                                fontSize = 12.sp,
                                                                maxLines = 1
                                                        )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                if (cartQuantity > 0) {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically,
                                                                modifier =
                                                                        Modifier.height(44.dp)
                                                                                .background(
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primaryContainer,
                                                                                        RoundedCornerShape(
                                                                                                50
                                                                                        )
                                                                                )
                                                                                .padding(
                                                                                        horizontal =
                                                                                                4.dp
                                                                                )
                                                        ) {
                                                                IconButton(
                                                                        onClick = onDecrementCart,
                                                                        modifier =
                                                                                Modifier.size(36.dp)
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .Remove,
                                                                                contentDescription =
                                                                                        "Remove",
                                                                                tint =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        )
                                                                }
                                                                Text(
                                                                        text =
                                                                                cartQuantity
                                                                                        .toString(),
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium
                                                                                        .copy(
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .Bold
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        horizontal =
                                                                                                8.dp
                                                                                )
                                                                )
                                                                IconButton(
                                                                        onClick = onIncrementCart,
                                                                        modifier =
                                                                                Modifier.size(36.dp)
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default.Add,
                                                                                contentDescription =
                                                                                        "Add",
                                                                                tint =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        )
                                                                }
                                                        }
                                                } else {
                                                        Button(
                                                                onClick = onAddToCartClick,
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        ),
                                                                shape = RoundedCornerShape(50),
                                                                contentPadding =
                                                                        PaddingValues(
                                                                                horizontal = 16.dp
                                                                        ),
                                                                modifier = Modifier.height(44.dp)
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .ShoppingCart,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(4.dp)
                                                                )
                                                                Text(
                                                                        "Add to Cart",
                                                                        fontSize = 12.sp,
                                                                        maxLines = 1
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-10).dp)) {

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                AttributeChip(
                                        icon = Icons.Outlined.WbSunny,
                                        label = "Light",
                                        value = "High"
                                )
                                AttributeChip(
                                        icon = Icons.Outlined.WaterDrop,
                                        label = "Water",
                                        value = "${plant.water_interval_days}d"
                                )
                                AttributeChip(
                                        icon = Icons.Default.Terrain,
                                        label = "Soil",
                                        value = plant.soil ?: "Standard"
                                )
                                AttributeChip(
                                        icon = Icons.Default.Star,
                                        label = "Difficulty",
                                        value = plant.difficulty ?: "Easy"
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isOwned) {
                                OwnedPlantDashboard(
                                        reminder = reminder,
                                        plant = plant,
                                        onWaterNow = onWaterNow,
                                        onArClick = onArClick,
                                        onToggleReminder = onToggleReminder,
                                        onIntervalChange = onIntervalChange
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "About",
                                style =
                                        MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = plant.description ?: "No description available.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Care Tips",
                                style =
                                        MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                        ),
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth()) {
                                CareCard(
                                        icon = Icons.Outlined.WbSunny,
                                        title = "Light",
                                        desc = "${plant.min_lux}-${plant.max_lux} Lux",
                                        modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                CareCard(
                                        icon = Icons.Outlined.WaterDrop,
                                        title = "Water",
                                        desc = "Every ${plant.water_interval_days} days",
                                        modifier = Modifier.weight(1f)
                                )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                }
        }
}

@Composable
fun AttributeChip(icon: ImageVector, label: String, value: String) {
        var expanded by remember { mutableStateOf(false) }

        Column(
                modifier =
                        Modifier.width(72.dp)
                                .clickable { expanded = !expanded }
                                .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .background(
                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.3f
                                                ),
                                                CircleShape
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                        )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )

                Text(
                        text = value,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                )
        }
}

@Composable
fun CareCard(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        desc: String,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier,
                shape = RoundedCornerShape(12.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column(modifier = Modifier.padding(16.dp)) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
        }
}
