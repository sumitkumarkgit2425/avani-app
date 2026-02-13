package com.example.navya.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navya.ui.components.MyPlantCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantsScreen(navController: NavController, viewModel: MyPlantsViewModel = hiltViewModel()) {
    val plants by viewModel.ownedPlants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            topBar = {
                TopAppBar(
                        windowInsets = WindowInsets(0.dp),
                        title = { Text("My Plants", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.background
                                )
                )
            }
    ) { paddingValues ->
        Box(
                modifier =
                        Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (plants.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No plants yet!", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Purchase plants from the Market to see them here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                ) {
                    items(plants) { plant ->
                        MyPlantCard(
                                plant = plant,
                                onClick = { navController.navigate("plant_detail/${plant.id}") }
                        )
                    }
                }
            }
        }
    }
}
