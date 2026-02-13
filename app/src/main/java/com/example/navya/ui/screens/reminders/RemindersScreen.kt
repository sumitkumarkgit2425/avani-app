package com.example.navya.ui.screens.reminders

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.navya.ui.components.ReminderCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(navController: NavController, viewModel: RemindersViewModel = hiltViewModel()) {
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var selectedFilter by remember { mutableStateOf("All") }

        val context = LocalContext.current
        var hasNotificationPermission by remember {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        mutableStateOf(
                                androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        )
                } else {
                        mutableStateOf(true)
                }
        }

        val permissionLauncher =
                rememberLauncherForActivityResult(
                        contract =
                                androidx.activity.result.contract.ActivityResultContracts
                                        .RequestPermission(),
                        onResult = { isGranted: Boolean ->
                                hasNotificationPermission = isGranted
                                if (!isGranted) {
                                        scope.launch {
                                                snackbarHostState.showSnackbar(
                                                        "Notifications needed for water reminders"
                                                )
                                        }
                                }
                        }
                )

        LaunchedEffect(Unit) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        if (!hasNotificationPermission) {
                                permissionLauncher.launch(
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                )
                        }
                }
        }

        LaunchedEffect(uiState.message) {
                uiState.message?.let {
                        scope.launch {
                                snackbarHostState.showSnackbar(it)
                                viewModel.clearMessage()
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
                topBar = {
                        CenterAlignedTopAppBar(
                                windowInsets = WindowInsets(0.dp),
                                title = {
                                        Text(
                                                "My Garden Tasks",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = colorScheme.background,
                                                titleContentColor = colorScheme.onBackground
                                        )
                        )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = colorScheme.background
        ) { innerPadding ->
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        val totalUpcoming = uiState.today.size + uiState.upcoming.size
                        val totalOverdue = uiState.overdue.size

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                Card(
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                colorScheme.secondaryContainer
                                                ),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "$totalUpcoming",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .displaySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.onSecondaryContainer
                                                )
                                                Text(
                                                        text = "To Water",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color =
                                                                colorScheme.onSecondaryContainer
                                                                        .copy(alpha = 0.8f)
                                                )
                                        }
                                }

                                Card(
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = colorScheme.errorContainer
                                                ),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                        text = "$totalOverdue",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .displaySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.onErrorContainer
                                                )
                                                Text(
                                                        text = "Overdue",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .labelMedium,
                                                        color =
                                                                colorScheme.onErrorContainer.copy(
                                                                        alpha = 0.8f
                                                                )
                                                )
                                        }
                                }
                        }

                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                listOf("All", "Overdue", "Today", "Upcoming").forEach { filter ->
                                        val isSelected = selectedFilter == filter
                                        FilterChip(
                                                selected = isSelected,
                                                onClick = { selectedFilter = filter },
                                                label = { Text(filter) },
                                                colors =
                                                        FilterChipDefaults.filterChipColors(
                                                                selectedContainerColor =
                                                                        colorScheme
                                                                                .primaryContainer,
                                                                selectedLabelColor =
                                                                        colorScheme
                                                                                .onPrimaryContainer
                                                        ),
                                                border =
                                                        FilterChipDefaults.filterChipBorder(
                                                                enabled = true,
                                                                selected = isSelected,
                                                                borderColor =
                                                                        if (isSelected)
                                                                                Color.Transparent
                                                                        else
                                                                                colorScheme.outline
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        )
                                                        )
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.isLoading) {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = colorScheme.primary) }
                        } else if (uiState.overdue.isEmpty() &&
                                        uiState.today.isEmpty() &&
                                        uiState.upcoming.isEmpty()
                        ) {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                        ) {
                                                Surface(
                                                        shape = CircleShape,
                                                        color = colorScheme.surfaceVariant,
                                                        modifier = Modifier.size(100.dp)
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Check,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        48.dp
                                                                                ),
                                                                        tint =
                                                                                colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                }
                                                Spacer(modifier = Modifier.height(24.dp))
                                                Text(
                                                        "All Caught Up!",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorScheme.onBackground
                                                )
                                                Text(
                                                        "Your garden is happy and hydrated.",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(top = 8.dp)
                                                )
                                                Spacer(modifier = Modifier.height(32.dp))
                                                Button(
                                                        onClick = {
                                                                navController.navigate(
                                                                        "market_screen"
                                                                )
                                                        },
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                colorScheme.primary
                                                                )
                                                ) { Text("Find More Plants") }
                                        }
                                }
                        } else {
                                val errorColor = colorScheme.error
                                val tertiaryColor = colorScheme.tertiary
                                val primaryColor = colorScheme.primary

                                LazyColumn(
                                        contentPadding =
                                                PaddingValues(
                                                        bottom = 80.dp,
                                                        start = 16.dp,
                                                        end = 16.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        fun renderSection(
                                                title: String,
                                                items: List<ReminderUiItem>,
                                                headerColor: Color
                                        ) {
                                                if (items.isNotEmpty() &&
                                                                (selectedFilter == "All" ||
                                                                        selectedFilter == title ||
                                                                        (selectedFilter ==
                                                                                "Upcoming" &&
                                                                                title ==
                                                                                        "Upcoming") ||
                                                                        (selectedFilter ==
                                                                                "Overdue" &&
                                                                                title ==
                                                                                        "Overdue") ||
                                                                        (selectedFilter ==
                                                                                "Today" &&
                                                                                title == "Today"))
                                                ) {
                                                        item {
                                                                Text(
                                                                        title,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        color = headerColor,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 16.dp,
                                                                                        bottom =
                                                                                                8.dp
                                                                                )
                                                                )
                                                        }
                                                        items(items) { item ->
                                                                ReminderCard(
                                                                        plantName = item.plantName,
                                                                        snippet = item.snippet,
                                                                        timeLabel = item.timeLabel,
                                                                        isOverdue = item.isOverdue,
                                                                        onWater = {
                                                                                viewModel
                                                                                        .markWatered(
                                                                                                item
                                                                                        )
                                                                        },
                                                                        onSnooze = {
                                                                                viewModel.snooze(
                                                                                        item
                                                                                )
                                                                        },
                                                                        onCancel = {
                                                                                viewModel
                                                                                        .cancelReminder(
                                                                                                item
                                                                                        )
                                                                        },
                                                                        imageUrl = item.plantImage
                                                                )
                                                        }
                                                }
                                        }

                                        renderSection("Overdue", uiState.overdue, errorColor)
                                        renderSection("Today", uiState.today, tertiaryColor)
                                        renderSection("Upcoming", uiState.upcoming, primaryColor)
                                }
                        }
                }
        }
}
