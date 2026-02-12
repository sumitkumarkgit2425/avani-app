package com.example.navya.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navya.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    val destination by viewModel.startDestination.collectAsState()

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        destination?.let { dest ->
            navController.navigate(dest) {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .background(
                    Color(0xFF2E7D32)
                ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = startAnimation,
                enter =
                    androidx.compose.animation.scaleIn(
                        initialScale = 0.5f,
                        animationSpec =
                            tween(
                                1000,
                                easing =
                                    androidx.compose.animation
                                        .core
                                        .FastOutSlowInEasing
                            )
                    ) + fadeIn(animationSpec = tween(800))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avanipng),
                    contentDescription = "Navya Logo",
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(
                visible = startAnimation,
                enter =
                    fadeIn(animationSpec = tween(1000, delayMillis = 500)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec =
                                    tween(1000, delayMillis = 500)
                            )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AVANI",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Buy plants. Care. Visualize. Grow.",
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            Color.White.copy(
                                alpha = 0.8f
                            ),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
