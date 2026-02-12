package com.example.navya.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.navya.ui.components.HomeReminderCard
import com.example.navya.ui.components.SmartToolCard
import com.example.navya.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
        val uiState by viewModel.uiState.collectAsState()

        val context = LocalContext.current
        val mainViewModel: com.example.navya.MainViewModel =
                hiltViewModel(context as androidx.activity.ComponentActivity)
        val cartState by mainViewModel.uiState.collectAsState()

        val activity = LocalContext.current as? android.app.Activity
        androidx.activity.compose.BackHandler { activity?.finish() }

        Scaffold(contentWindowInsets = WindowInsets(0.dp)) { innerPadding ->
                Column(
                        modifier =
                                Modifier.padding(innerPadding)
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(start = 7.dp, end = 12.dp, bottom = 12.dp)
                ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(130.dp)
                                                .shadow(
                                                        elevation = 8.dp,
                                                        shape =
                                                                RoundedCornerShape(
                                                                        bottomStart = 32.dp,
                                                                        bottomEnd = 32.dp
                                                                ),
                                                        spotColor = Color(0x40000000)
                                                )
                                                .background(
                                                        brush =
                                                                Brush.verticalGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        Color(
                                                                                                0xFF134E5E
                                                                                        ),
                                                                                        Color(
                                                                                                0xFF71B280
                                                                                        )
                                                                                )
                                                                ),
                                                        shape =
                                                                RoundedCornerShape(
                                                                        bottomStart = 32.dp,
                                                                        bottomEnd = 32.dp
                                                                )
                                                )
                                                .clip(
                                                        RoundedCornerShape(
                                                                bottomStart = 32.dp,
                                                                bottomEnd = 32.dp
                                                        )
                                                )
                        ) {
            Row(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 8.dp
                        ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                                        Column(
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.weight(1f)
                                        ) {

                                                val currentHour =
                                                        java.util.Calendar.getInstance()
                                                                .get(java.util.Calendar.HOUR_OF_DAY)
                                                val greetingText =
                                                        when (currentHour) {
                                                                in 5..11 -> "Good Morning,"
                                                                in 12..16 -> "Good Afternoon,"
                                                                in 17..20 -> "Good Evening,"
                                                                else -> "Good Night,"
                                                        }
                                                val displayName =
                                                        cartState
                                                                .userName
                                                                ?.split(" ")
                                                                ?.firstOrNull()
                                                                ?: "Sumit"


                                                val greetingAlpha =
                                                        androidx.compose.runtime.remember {
                                                                androidx.compose.animation.core
                                                                        .Animatable(0f)
                                                        }
                                                val nameAlpha =
                                                        androidx.compose.runtime.remember {
                                                                androidx.compose.animation.core
                                                                        .Animatable(0f)
                                                        }
                                                val greetingOffset =
                                                        androidx.compose.runtime.remember {
                                                                androidx.compose.animation.core
                                                                        .Animatable(
                                                                                40f
                                                                        )
                                                        }
                                                val nameOffset =
                                                        androidx.compose.runtime.remember {
                                                                androidx.compose.animation.core
                                                                        .Animatable(40f)
                                                        }

                                                androidx.compose.runtime.LaunchedEffect(Unit) {

                                                        val springSpec =
                                                                androidx.compose.animation.core
                                                                        .spring<Float>(
                                                                                dampingRatio =
                                                                                        androidx.compose
                                                                                                .animation
                                                                                                .core
                                                                                                .Spring
                                                                                                .DampingRatioMediumBouncy,
                                                                                stiffness =
                                                                                        androidx.compose
                                                                                                .animation
                                                                                                .core
                                                                                                .Spring
                                                                                                .StiffnessMediumLow
                                                                        )

                                                        launch {
                                                                greetingAlpha.animateTo(
                                                                        1f,
                                                                        androidx.compose.animation
                                                                                .core.tween(
                                                                                500
                                                                        )
                                                                )
                                                        }
                                                        launch {
                                                                greetingOffset.animateTo(
                                                                        0f,
                                                                        springSpec
                                                                )
                                                        }

                                                        kotlinx.coroutines.delay(50)

                                                        launch {
                                                                nameAlpha.animateTo(
                                                                        1f,
                                                                        androidx.compose.animation
                                                                                .core.tween(500)
                                                                )
                                                        }
                                                        launch {
                                                                nameOffset.animateTo(0f, springSpec)
                                                        }
                                                }

                                                Text(
                                                        text = greetingText,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        color = Color.White.copy(alpha = 0.8f),
                                                        fontWeight = FontWeight.Medium,
                                                        modifier =
                                                                Modifier.alpha(greetingAlpha.value)
                                                                        .offset(
                                                                                y =
                                                                                        greetingOffset
                                                                                                .value
                                                                                                .dp
                                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = displayName,
                                                style =
                                                    MaterialTheme.typography
                                                        .displaySmall,
                                                fontWeight = FontWeight.Bold,
                                                color =
                                                    Color(
                                                        0xFFFFD700
                                                    ), // Premium Gold color
                                                modifier =
                                                    Modifier.alpha(nameAlpha.value)
                                                        .offset(
                                                            y =
                                                                nameOffset
                                                                    .value
                                                                    .dp
                                                        )
                                            )
                                        }


                                        val cartCount = cartState.cartItems.sumOf { it.quantity }
                                        IconButton(
                                                onClick = { mainViewModel.openCart() },
                                                modifier =
                                                        Modifier.padding(bottom = 20.dp)
                                                                .size(48.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.15f
                                                                        ),
                                                                        androidx.compose.foundation
                                                                                .shape.CircleShape
                                                                )
                                        ) {
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
                                                                contentDescription = "Cart",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(24.dp)
                                                        )
                                                }
                                        }
                                }
                        }


                        Box(
                                modifier =
                                        Modifier.padding(top = 24.dp)
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .padding(bottom = 24.dp)
                                                .clip(RoundedCornerShape(24.dp))
                        ) {
                                androidx.compose.foundation.Image(
                                        painter =
                                                painterResource(
                                                        id = R.drawable.home_visual
                                                ),
                                        contentDescription = "Featured Home Visual",
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                )
                        }

                        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                                com.example.navya.ui.components.NavyaSearchBar(
                                        query = "",
                                        onQueryChange = {},
                                        placeholder = "Search plants...",
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth()
                                )

                                Box(
                                        modifier =
                                                Modifier.matchParentSize().clickable {
                                                        navController.navigate("search_screen")
                                                }
                                )
                        }


                        Text(
                                "Smart Tools",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                        )


                        Row(modifier = Modifier.fillMaxWidth()) {
                                SmartToolCard(
                                        title = "Check Light",
                                        subtitle = "Intensity",
                                        buttonText = "Measure Now",
                                        icon = Icons.Default.WbSunny,
                                        buttonColor =
                                                MaterialTheme.colorScheme.primary,
                                        backgroundColor =
                                                MaterialTheme.colorScheme
                                                        .primaryContainer,
                                        onClick = { navController.navigate("light_meter_screen") },
                                        modifier = Modifier.weight(1f)
                                )
                                SmartToolCard(
                                        title = "Virtual",
                                        subtitle = "Placement",
                                        buttonText = "Try in AR",
                                        icon = Icons.Default.CameraAlt,
                                        buttonColor =
                                                MaterialTheme.colorScheme.primary,
                                        backgroundColor =
                                                MaterialTheme.colorScheme
                                                        .tertiaryContainer,
                                        onClick = { navController.navigate("ar_screen") },
                                        modifier = Modifier.weight(1f)
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))


                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        "Plant Marketplace",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                        onClick = {
                                                navController.navigate("market_screen") {
                                                        popUpTo(
                                                                navController
                                                                        .graph
                                                                        .startDestinationId
                                                        ) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                }
                                        }
                                ) { Text("See All") }
                        }


                        if (uiState.isLoading) {
                                Box(
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                        } else {
                                LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding =
                                                PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                                ) {
                                        items(uiState.filteredPlants) { plant ->
                                                com.example.navya.ui.components.MarketPlantCard(
                                                        plant = plant,
                                                        quantity =
                                                                mainViewModel
                                                                        .getCartQuantity(plant.id)
                                                                        .collectAsState(initial = 0)
                                                                        .value,
                                                        onAddToCart = {
                                                                mainViewModel.incrementCartItem(
                                                                        plant
                                                                )
                                                        },
                                                        onIncrement = {
                                                                mainViewModel.incrementCartItem(
                                                                        plant
                                                                )
                                                        },
                                                        onDecrement = {
                                                                mainViewModel.decrementCartItem(
                                                                        plant
                                                                )
                                                        },
                                                        onClick = {
                                                                navController.navigate(
                                                                        "plant_detail/${plant.id}"
                                                                )
                                                        },
                                                        modifier = Modifier.width(200.dp)
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))


                        if (uiState.upcomingReminder != null && uiState.reminderPlantName != null) {
                                Text(
                                        "Upcoming Reminders",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                )
                                HomeReminderCard(
                                        plantName = uiState.reminderPlantName!!,
                                        timeLabel = uiState.reminderTimeLabel ?: "Soon",
                                        onClick = {
                                                navController.navigate("reminders_screen") {
                                                        popUpTo(
                                                                navController
                                                                        .graph
                                                                        .startDestinationId
                                                        ) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                }
                                        }
                                )
                        }
                }
        }
}
