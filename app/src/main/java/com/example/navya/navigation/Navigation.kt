package com.example.navya.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.navya.ui.components.PlantARView
import com.example.navya.ui.screens.auth.LoginScreen
import com.example.navya.ui.screens.auth.RegisterScreen
import com.example.navya.ui.screens.cart.CartScreen
import com.example.navya.ui.screens.details.PlantDetailScreen
import com.example.navya.ui.screens.home.HomeScreen
import com.example.navya.ui.screens.lightmeter.LightMeterScreen
import com.example.navya.ui.screens.market.MarketScreen
import com.example.navya.ui.screens.onboarding.OnboardingScreen
import com.example.navya.ui.screens.profile.MyPlantsScreen
import com.example.navya.ui.screens.profile.ProfileScreen
import com.example.navya.ui.screens.reminders.RemindersScreen
import com.example.navya.ui.screens.search.SearchScreen
import com.example.navya.ui.screens.splash.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavyaNavGraph(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        startDestination: String = "splash_screen"
) {
        NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
        ) {
                val tabEnterTransition = {
                        fadeIn(tween(400)) +
                                scaleIn(initialScale = 0.96f, animationSpec = tween(400))
                }
                val tabExitTransition = {
                        androidx.compose.animation.fadeOut(tween(400)) +
                                androidx.compose.animation.scaleOut(
                                        targetScale = 1.04f,
                                        animationSpec = tween(400)
                                )
                }

                composable(
                        "splash_screen",
                        enterTransition = { fadeIn(tween(300)) },
                        exitTransition = { androidx.compose.animation.fadeOut(tween(300)) }
                ) { SplashScreen(navController) }

                composable(
                        "onboarding",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { OnboardingScreen(navController) }

                composable(
                        "login_screen",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { LoginScreen(navController) }

                composable(
                        "register_screen",
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { -it } }
                ) { RegisterScreen(navController) }

                composable(
                        "home_screen",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { HomeScreen(navController) }

                composable(
                        "market_screen",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { MarketScreen(navController) }

                composable(
                        "reminders_screen",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { RemindersScreen(navController) }

                composable(
                        "profile_screen",
                        enterTransition = { tabEnterTransition() },
                        exitTransition = { tabExitTransition() }
                ) { ProfileScreen(navController) }

                composable(
                        "my_plants_screen",
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { -it } }
                ) { MyPlantsScreen(navController) }

                val slideInTransition = {
                        androidx.compose.animation.slideInHorizontally { it } + fadeIn(tween(300))
                }
                val slideOutTransition = {
                        androidx.compose.animation.slideOutHorizontally { -it } +
                                androidx.compose.animation.fadeOut(
                                        androidx.compose.animation.core.tween(300)
                                )
                }

                composable(
                        "cart_screen",
                        enterTransition = { slideInTransition() },
                        popExitTransition = {
                                androidx.compose.animation.slideOutHorizontally { it }
                        }
                ) { CartScreen(navController) }

                composable(
                        "search_screen",
                        enterTransition = { slideInTransition() },
                        popExitTransition = { slideOutHorizontally { it } }
                ) { SearchScreen(navController) }

                composable(
                        "light_meter_screen",
                        enterTransition = { slideInTransition() },
                        popExitTransition = { slideOutHorizontally { it } }
                ) { LightMeterScreen(navController) }

                composable(
                        "ar_screen?encodedImage={encodedImage}",
                        arguments =
                                listOf(
                                        navArgument("encodedImage") {
                                                type = NavType.StringType
                                                defaultValue = ""
                                        }
                                ),
                        enterTransition = { slideInTransition() },
                        popExitTransition = { slideOutHorizontally { it } }
                ) { backStackEntry ->
                        val encodedImage = backStackEntry.arguments?.getString("encodedImage") ?: ""
                        val decodedImage =
                                if (encodedImage.isNotEmpty()) {
                                        URLDecoder.decode(
                                                encodedImage,
                                                StandardCharsets.UTF_8.toString()
                                        )
                                } else ""
                        PlantARView(navController, decodedImage)
                }

                composable(
                        "plant_detail/{plantId}",
                        arguments = listOf(navArgument("plantId") { type = NavType.StringType }),
                        enterTransition = { slideInTransition() },
                        popExitTransition = { slideOutHorizontally { it } }
                ) { backStackEntry ->
                        val plantId = backStackEntry.arguments?.getString("plantId")
                        if (plantId != null) {
                                PlantDetailScreen(navController, plantId)
                        }
                }
        }
}
