package com.example.navya.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_screen", Icons.Default.Home, "Home")
    object Market : BottomNavItem("market_screen", Icons.Default.Store, "Market")
    object Reminders : BottomNavItem("reminders_screen", Icons.Default.Notifications, "Reminders")
    object Profile : BottomNavItem("profile_screen", Icons.Default.Person, "Profile")
}

@Composable
fun NavyaBottomBar(navController: NavController) {
    val items =
            listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Market,
                    BottomNavItem.Reminders,
                    BottomNavItem.Profile
            )

    NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
            )
        }
    }
}
