package com.example.navya

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navya.ui.components.NavyaBottomBar
import com.example.navya.ui.theme.NavyaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : ComponentActivity(), com.razorpay.PaymentResultWithDataListener {

    @javax.inject.Inject
    lateinit var paymentRepository: com.example.navya.data.repository.PaymentRepository

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK))

        try {
            com.razorpay.Checkout.preload(applicationContext)
        } catch (e: Exception) {

            Toast.makeText(this, "Razorpay init failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        setContent {
            val mainUiState by mainViewModel.uiState.collectAsState()
            val isDarkTheme =
                    mainUiState.isDarkTheme ?: androidx.compose.foundation.isSystemInDarkTheme()

            NavyaTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    val navigateTo = intent.getStringExtra("navigate_to")
                    if (navigateTo == "reminders_screen") {
                        navController.navigate("reminders_screen")
                        intent.removeExtra("navigate_to")
                    }
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar =
                        currentRoute in
                                listOf(
                                        "home_screen",
                                        "market_screen",
                                        "reminders_screen",
                                        "profile_screen"
                                )

                val mainUiState by mainViewModel.uiState.collectAsState()
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    mainViewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.ShowToast -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                if (showBottomBar) {
                                    NavyaBottomBar(navController = navController)
                                }
                            }
                    ) { innerPadding ->
                        com.example.navya.navigation.NavyaNavGraph(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                        )
                    }

                    com.example.navya.ui.screens.market.CartDrawer(
                            isOpen = mainUiState.isCartOpen,
                            cartItems = mainUiState.cartItems,
                            totalAmount = mainUiState.totalAmount,
                            onClose = { mainViewModel.closeCart() },
                            onIncrement = { mainViewModel.increment(it) },
                            onDecrement = { mainViewModel.decrement(it) },
                            onCheckout = {
                                mainViewModel.closeCart()
                                startPayment(
                                        amount = mainUiState.totalAmount,
                                        email = "user@example.com",
                                        contact = "9999999999"
                                )
                            },
                            onItemClick = { plantId ->
                                mainViewModel.closeCart()
                                navController.navigate("plant_detail/$plantId")
                            }
                    )

                    Spacer(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .windowInsetsTopHeight(WindowInsets.statusBars)
                                            .background(Color.Black)
                                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }

    private fun startPayment(amount: Double, email: String, contact: String) {
        val activity: android.app.Activity = this

        try {
            val co = com.razorpay.Checkout()
            co.setKeyID("rzp_test_RB8uACuI3uJG0E")

            val options = org.json.JSONObject()
            options.put("name", "Avani")
            options.put("description", "Garden Supplies")
            options.put("theme.color", "#2E7D32")
            options.put("currency", "INR")
            options.put("amount", (amount * 100).toInt())
            options.put("retry", org.json.JSONObject().put("enabled", true).put("max_count", 2))
            options.put("send_sms_hash", true)

            co.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Error starting payment: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                Toast.makeText(this, "Check logcat for details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPaymentSuccess(
            razorpayPaymentId: String?,
            paymentData: com.razorpay.PaymentData?
    ) {
        val orderId = paymentData?.orderId ?: "unknown_order"
        mainViewModel.handlePaymentSuccess(orderId)
    }

    override fun onPaymentError(
            code: Int,
            response: String?,
            paymentData: com.razorpay.PaymentData?
    ) {
        mainViewModel.handlePaymentError(code, response)
    }
}
