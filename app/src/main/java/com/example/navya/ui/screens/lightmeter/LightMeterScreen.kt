package com.example.navya.ui.screens.lightmeter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navya.ui.components.MarketPlantCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightMeterScreen(
        navController: NavController,
        viewModel: LightMeterViewModel = hiltViewModel()
) {
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(contentWindowInsets = WindowInsets(0.dp)) { innerPadding ->
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                                Text(
                                        "Light Meter",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Column(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                when {
                                        uiState.isScanning -> {

                                                val animatedScanProgress by
                                                        animateFloatAsState(
                                                                targetValue = uiState.scanProgress,
                                                                animationSpec =
                                                                        tween(
                                                                                durationMillis =
                                                                                        100,
                                                                                easing =
                                                                                        androidx.compose
                                                                                                .animation
                                                                                                .core
                                                                                                .LinearEasing
                                                                        ),
                                                                label = "scan_progress"
                                                        )

                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier = Modifier.fillMaxWidth()
                                                ) {
                                                        LightIntensityGauge(
                                                                lux = uiState.currentLux,
                                                                condition = uiState.lightCondition
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

                                                        Box(contentAlignment = Alignment.Center) {
                                                                CircularProgressIndicator(
                                                                        progress = {
                                                                                animatedScanProgress
                                                                        },
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        80.dp
                                                                                ),
                                                                        strokeWidth = 8.dp,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        trackColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant,
                                                                        strokeCap = StrokeCap.Round
                                                                )
                                                                Text(
                                                                        text =
                                                                                "${(uiState.scanProgress * 100).toInt()}%",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelSmall,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Text(
                                                                "Analyzing Light...",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                                "Hold your phone steady",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }
                                        uiState.scanError != null -> {

                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        modifier = Modifier.fillMaxWidth()
                                                ) {
                                                        LightIntensityGauge(
                                                                lux = uiState.currentLux,
                                                                condition = uiState.lightCondition
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

                                                        Box(
                                                                modifier =
                                                                        Modifier.size(80.dp)
                                                                                .background(
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .errorContainer,
                                                                                        CircleShape
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Info,
                                                                        contentDescription =
                                                                                "Error",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error,
                                                                        modifier =
                                                                                Modifier.size(40.dp)
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Text(
                                                                text = "Measurement Problem",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .error
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                                text = uiState.scanError
                                                                                ?: "Unknown error",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant,
                                                                textAlign = TextAlign.Center,
                                                                modifier =
                                                                        Modifier.padding(
                                                                                horizontal = 32.dp
                                                                        )
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

                                                        Button(
                                                                onClick = { viewModel.startScan() },
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .error
                                                                        ),
                                                                modifier = Modifier.height(50.dp)
                                                        ) { Text("Try Again") }
                                                }
                                        }
                                        !uiState.isResultAvailable -> {

                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        LightIntensityGauge(
                                                                lux = uiState.currentLux,
                                                                condition = uiState.lightCondition
                                                        )

                                                        Spacer(modifier = Modifier.height(32.dp))

                                                        Text(
                                                                text =
                                                                        "Point your camera at the light source",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                textAlign = TextAlign.Center,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        Button(
                                                                onClick = { viewModel.startScan() },
                                                                modifier =
                                                                        Modifier.fillMaxWidth(0.7f)
                                                                                .height(56.dp),
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        ),
                                                                elevation =
                                                                        ButtonDefaults
                                                                                .buttonElevation(
                                                                                        defaultElevation =
                                                                                                6.dp,
                                                                                        pressedElevation =
                                                                                                2.dp
                                                                                )
                                                        ) {
                                                                Text(
                                                                        "Start Measurement",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium
                                                                                        .copy(
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .Bold
                                                                                        )
                                                                )
                                                        }
                                                }
                                        }
                                        else -> {

                                                LazyColumn(
                                                        modifier = Modifier.fillMaxSize(),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(16.dp),
                                                        contentPadding =
                                                                PaddingValues(
                                                                        top = 10.dp,
                                                                        bottom = 24.dp
                                                                )
                                                ) {
                                                        item {
                                                                LightIntensityGauge(
                                                                        lux = uiState.currentLux,
                                                                        condition =
                                                                                uiState.lightCondition
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )
                                                        }

                                                        item {
                                                                Text(
                                                                        text =
                                                                                "Recommended plants for this light:",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .padding(
                                                                                                bottom =
                                                                                                        8.dp
                                                                                        )
                                                                )
                                                        }

                                                        if (uiState.recommendedPlants.isEmpty()) {
                                                                item {
                                                                        Box(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth()
                                                                                                .padding(
                                                                                                        32.dp
                                                                                                ),
                                                                                contentAlignment =
                                                                                        Alignment
                                                                                                .Center
                                                                        ) {
                                                                                Text(
                                                                                        text =
                                                                                                "No specific plants found for this exact range.\nTry a different spot!",
                                                                                        textAlign =
                                                                                                TextAlign
                                                                                                        .Center,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                }
                                                        } else {
                                                                items(uiState.recommendedPlants) {
                                                                        plant ->
                                                                        MarketPlantCard(
                                                                                plant = plant,
                                                                                onAddToCart = {},
                                                                                onClick = {
                                                                                        selectedPlant
                                                                                        ->
                                                                                        navController
                                                                                                .navigate(
                                                                                                        "plant_detail/${selectedPlant.id}"
                                                                                                )
                                                                                }
                                                                        )
                                                                }
                                                        }

                                                        item {
                                                                Button(
                                                                        onClick = {
                                                                                viewModel
                                                                                        .resetScan()
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .padding(
                                                                                                top =
                                                                                                        8.dp
                                                                                        )
                                                                                        .height(
                                                                                                50.dp
                                                                                        ),
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                ) { Text("Measure Again") }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun LightIntensityGauge(lux: Float, condition: String) {

        val animatedLux by
                animateFloatAsState(
                        targetValue = lux,
                        animationSpec =
                                tween(
                                        durationMillis = 800,
                                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                                ),
                        label = "lux"
                )

        val maxLux = 5000f
        val progress = (animatedLux / maxLux).coerceIn(0f, 1f)

        val animatedProgress by
                animateFloatAsState(
                        targetValue = progress,
                        animationSpec =
                                tween(
                                        durationMillis = 800,
                                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                                ),
                        label = "progress"
                )

        Card(
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(
                                                color = Color.LightGray.copy(alpha = 0.3f),
                                                startAngle = 140f,
                                                sweepAngle = 260f,
                                                useCenter = false,
                                                style =
                                                        androidx.compose.ui.graphics.drawscope
                                                                .Stroke(
                                                                        width = 40f,
                                                                        cap = StrokeCap.Round
                                                                )
                                        )

                                        val gradient =
                                                androidx.compose.ui.graphics.Brush.sweepGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF4CAF50),
                                                                        Color(0xFFFFEB3B),
                                                                        Color(0xFFFF9800),
                                                                        Color(0xFFF44336)
                                                                ),
                                                        center = center
                                                )

                                        drawArc(
                                                brush = gradient,
                                                startAngle = 140f,
                                                sweepAngle = 260f * animatedProgress,
                                                useCenter = false,
                                                style =
                                                        androidx.compose.ui.graphics.drawscope
                                                                .Stroke(
                                                                        width = 40f,
                                                                        cap = StrokeCap.Round
                                                                )
                                        )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                                text = "${animatedLux.toInt()}",
                                                style =
                                                        MaterialTheme.typography.displayMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurface,
                                                                fontFeatureSettings = "tnum"
                                                        )
                                        )
                                        Text(
                                                text = "LUX",
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                fontWeight = FontWeight.Bold,
                                                                letterSpacing = 1.sp
                                                        )
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        androidx.compose.foundation.layout.Box(
                                modifier = Modifier.height(48.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                        ) {
                                androidx.compose.foundation.layout.Box(
                                        modifier =
                                                Modifier.background(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer,
                                                                shape =
                                                                        androidx.compose.foundation
                                                                                .shape
                                                                                .RoundedCornerShape(
                                                                                        50
                                                                                )
                                                        )
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        )
                                ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer,
                                                        modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.size(8.dp))
                                                Text(
                                                        text = condition,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onPrimaryContainer,
                                                        fontWeight = FontWeight.Medium,
                                                        textAlign = TextAlign.Center,
                                                        maxLines = 1,
                                                        overflow =
                                                                androidx.compose.ui.text.style
                                                                        .TextOverflow.Ellipsis
                                                )
                                        }
                                }
                        }
                }
        }
}
