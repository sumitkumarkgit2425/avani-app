package com.example.navya.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showAboutSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.toggleNotifications(true)
            } else {
                viewModel.toggleNotifications(false)
                Toast.makeText(
                    context,
                    "Notifications permission denied",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    androidx.activity.compose.BackHandler {
        navController.navigate("home_screen") {
            popUpTo("home_screen") { inclusive = true }
            launchSingleTop = true
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier =
                Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProfileHeader(
                uiState = uiState,
                onImageSelected = { viewModel.updateProfileImage(it) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionHeader("My Garden")
                ProfileCard {
                    Column {
                        ProfileOptionRow(
                            title = "My Plants",
                            subtitle = "View purchased plants",
                            icon = Icons.Outlined.Eco,
                            onClick = {
                                navController.navigate(
                                    "my_plants_screen"
                                )
                            }
                        )

                        HorizontalDivider(
                            color =
                                MaterialTheme.colorScheme
                                    .surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                        )
                        ProfileOptionRow(
                            title = "Reminders",
                            subtitle = "Manage watering reminders",
                            icon = Icons.Outlined.Notifications,
                            onClick = {
                                navController.navigate(
                                    "reminders_screen"
                                ) {
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

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionHeader("App Settings")
                ProfileCard {
                    Column {
                        ProfileToggleRow(
                            title = "Notifications",
                            subtitle = "Receive care tips & alerts",
                            icon = Icons.Outlined.NotificationsActive,
                            checked = uiState.isNotificationsEnabled,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    if (Build.VERSION.SDK_INT >=
                                        Build.VERSION_CODES
                                            .TIRAMISU
                                    ) {
                                        if (ContextCompat
                                                .checkSelfPermission(
                                                    context,
                                                    Manifest.permission
                                                        .POST_NOTIFICATIONS
                                                ) ==
                                            PackageManager
                                                .PERMISSION_GRANTED
                                        ) {
                                            viewModel
                                                .toggleNotifications(
                                                    true
                                                )
                                        } else {
                                            notificationPermissionLauncher
                                                .launch(
                                                    Manifest.permission
                                                        .POST_NOTIFICATIONS
                                                )
                                        }
                                    } else {
                                        viewModel
                                            .toggleNotifications(
                                                true
                                            )
                                    }
                                } else {
                                    viewModel
                                        .toggleNotifications(
                                            false
                                        )
                                }
                            }
                        )
                        HorizontalDivider(
                            color =
                                MaterialTheme.colorScheme
                                    .surfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                        )
                        ProfileToggleRow(
                            title = "Dark Theme",
                            subtitle = "Switch to dark mode",
                            icon = Icons.Outlined.DarkMode,
                            checked = uiState.isDarkTheme,
                            onCheckedChange = {
                                viewModel.toggleTheme(it)
                            }
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Support")
                    ProfileCard {
                        Column {
                            ProfileOptionRow(
                                title = "About Avani",
                                subtitle = "App info & purpose",
                                icon = Icons.Outlined.Info,
                                onClick = { showAboutSheet = true }
                            )
                            HorizontalDivider(
                                color =
                                    MaterialTheme.colorScheme
                                        .surfaceVariant
                                        .copy(alpha = 0.5f)
                            )
                            ProfileOptionRow(
                                title = "Log Out",
                                subtitle = null,
                                icon = Icons.Outlined.ExitToApp,
                                iconTint =
                                    MaterialTheme.colorScheme
                                        .error,
                                textColor =
                                    MaterialTheme.colorScheme
                                        .error,
                                showArrow = false,
                                onClick = {
                                    viewModel.logout()
                                    navController.navigate(
                                        "login_screen"
                                    ) {
                                        popUpTo(0) {
                                            inclusive =
                                                true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showAboutSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAboutSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) { AboutAvaniContent() }
        }
    }
}

@Composable
fun AboutAvaniContent() {
    Column(
        modifier =
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier.size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Avani",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text =
                "Your digital gardening companion. Avani helps you track your plants, get timely watering reminders, and explore our green marketplace for new additions to your home.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "© 2026 Avani Inc. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileHeader(uiState: ProfileUiState, onImageSelected: (android.net.Uri) -> Unit) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    onImageSelected(uri)
                }
            }
        )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier =
                    Modifier.weight(0.35f)
                        .aspectRatio(1f)
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                MaterialTheme.colorScheme
                                    .secondaryContainer
                            )
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly
                                    )
                                )
                            },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.photoUrl != null) {
                        AsyncImage(
                            model =
                                coil.request.ImageRequest.Builder(
                                    LocalContext.current
                                )
                                    .data(uiState.photoUrl)
                                    .diskCachePolicy(
                                        coil.request
                                            .CachePolicy
                                            .ENABLED
                                    )
                                    .memoryCachePolicy(
                                        coil.request
                                            .CachePolicy
                                            .ENABLED
                                    )
                                    .networkCachePolicy(
                                        coil.request
                                            .CachePolicy
                                            .ENABLED
                                    )
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint =
                                MaterialTheme.colorScheme
                                    .onSecondaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Surface(
                    modifier =
                        Modifier.align(Alignment.BottomEnd)
                            .offset(
                                x = 6.dp,
                                y = 6.dp
                            )
                            .size(32.dp),
                    shape = CircleShape,
                    color =
                        MaterialTheme.colorScheme
                            .surface,
                    shadowElevation = 4.dp,
                    border =
                        androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                ) {
                    Box(
                        modifier =
                            Modifier.fillMaxSize().clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint =
                                MaterialTheme.colorScheme
                                    .primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(0.6f).padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text =
                        if (uiState.displayName.isNotEmpty())
                            uiState.displayName
                        else "User",
                    style =
                        MaterialTheme.typography
                            .headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                        if (!uiState.email.isNullOrBlank()) uiState.email
                        else "No email linked",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            alpha = 0.8f
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun ProfileCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) { content() }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun ProfileOptionRow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    showArrow: Boolean = true
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProfileToggleRow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor =
                        MaterialTheme.colorScheme.primaryContainer,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
        )
    }
}
