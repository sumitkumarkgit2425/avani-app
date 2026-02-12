package com.example.navya.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.navya.R
import com.example.navya.ui.components.NavyaTextField
import com.example.navya.ui.viewmodels.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val credentialManager = CredentialManager.create(context)
        val focusManager = LocalFocusManager.current
        val activity = context as? android.app.Activity

        val webClientId = "156084831588-llslkaa5rptoqug5eb58ekdvapdskbv3.apps.googleusercontent.com"

        fun onGoogleSignInClicked() {
                coroutineScope.launch {
                        try {
                                val googleIdOption =
                                        GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId(webClientId)
                                                .setAutoSelectEnabled(true)
                                                .build()

                                val request =
                                        GetCredentialRequest.Builder()
                                                .addCredentialOption(googleIdOption)
                                                .build()

                                val result =
                                        credentialManager.getCredential(
                                                context = context,
                                                request = request
                                        )

                                val credential = result.credential
                                if (credential is CustomCredential &&
                                                credential.type ==
                                                        GoogleIdTokenCredential
                                                                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                        val googleIdTokenCredential =
                                                GoogleIdTokenCredential.createFrom(credential.data)

                                        viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                }
                        } catch (e: Exception) {}
                }
        }

        LaunchedEffect(viewModel.isLoginSuccessful) {
                if (viewModel.isLoginSuccessful) {
                        navController.navigate("home_screen") {
                                popUpTo("login_screen") { inclusive = true }
                        }
                }
        }

        LaunchedEffect(viewModel.loginError) {
                viewModel.loginError?.let { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
        }

        val fullPageGradient =
                Brush.linearGradient(
                        colors =
                                listOf(
                                        Color(0xFF66BB6A).copy(alpha = 0.8f),
                                        Color(0xFF9181F4).copy(alpha = 0.8f),
                                ),
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                )

        Box(
                modifier =
                        Modifier.fillMaxSize().background(fullPageGradient).pointerInput(Unit) {
                                detectTapGestures(onTap = { focusManager.clearFocus() })
                        },
                contentAlignment = Alignment.Center
        ) {
                if (viewModel.showPhoneInputDialog) {
                        AlertDialog(
                                onDismissRequest = { viewModel.showPhoneInputDialog = false },
                                confirmButton = {
                                        TextButton(
                                                onClick = {
                                                        viewModel.showPhoneInputDialog = false
                                                        val activity =
                                                                context as? android.app.Activity
                                                        activity?.let {
                                                                viewModel.onPhoneLoginClicked(it)
                                                        }
                                                }
                                        ) { Text("Send OTP") }
                                },
                                title = { Text("Phone Login") },
                                text = {
                                        OutlinedTextField(
                                                value = viewModel.phoneNumber,
                                                onValueChange = { viewModel.phoneNumber = it },
                                                label = { Text("Enter Phone Number (with +91)") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Phone
                                                        )
                                        )
                                }
                        )
                }
                if (viewModel.showOtpSheet) {
                        OtpBottomSheet(
                                viewModel = viewModel,
                                onDismiss = { viewModel.showOtpSheet = false }
                        )
                }
                Card(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                        Column(
                                modifier =
                                        Modifier.padding(24.dp)
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Icon(
                                        painter = painterResource(id = R.drawable.avanipng),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(100.dp)
                                )

                                Text(
                                        text = "Login",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                        text = "Enter your email and password to log in",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                )

                                NavyaTextField(
                                        value = viewModel.emailText,
                                        onValueChange = { viewModel.emailText = it },
                                        label = "Email Address",
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Email,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                )
                                        },
                                        singleLine = true,
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Email,
                                                        imeAction = ImeAction.Next
                                                )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                NavyaTextField(
                                        value = viewModel.passwordText,
                                        onValueChange = { viewModel.passwordText = it },
                                        label = "Password",
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation =
                                                if (viewModel.isPasswordVisible)
                                                        VisualTransformation.None
                                                else PasswordVisualTransformation(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Lock,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                )
                                        },
                                        trailingIcon = {
                                                val image =
                                                        if (viewModel.isPasswordVisible)
                                                                Icons.Default.Visibility
                                                        else Icons.Default.VisibilityOff
                                                IconButton(
                                                        onClick = {
                                                                viewModel.isPasswordVisible =
                                                                        !viewModel.isPasswordVisible
                                                        }
                                                ) {
                                                        Icon(
                                                                image,
                                                                contentDescription = null,
                                                                tint = Color.Gray
                                                        )
                                                }
                                        },
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Password,
                                                        imeAction = ImeAction.Done
                                                ),
                                        singleLine = true
                                )

                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                        checked = viewModel.isRemembered,
                                                        onCheckedChange = {
                                                                viewModel.isRemembered = it
                                                        }
                                                )
                                                Text("Remember me", fontSize = 12.sp)
                                        }
                                        TextButton(onClick = { /* Handle Forgot */}) {
                                                Text(
                                                        "Forgot Password?",
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontSize = 12.sp
                                                )
                                        }
                                }

                                Button(
                                        onClick = { viewModel.onLoginClicked() },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        enabled = !viewModel.isLoading,
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                        if (viewModel.isLoading) {
                                                CircularProgressIndicator(
                                                        color = Color.White,
                                                        modifier = Modifier.size(24.dp),
                                                        strokeWidth = 2.dp
                                                )
                                        } else {
                                                Text(
                                                        "Log In",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                )
                                        }
                                }

                                Text(
                                        text = "Or login with",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                        SocialIcon(
                                                iconId = R.drawable.google,
                                                onClick = { onGoogleSignInClicked() }
                                        )

                                        SocialIcon(
                                                iconId = R.drawable.phone,
                                                onClick = { viewModel.onPhoneIconClicked() }
                                        )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                                text = "Don't have an account? ",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        TextButton(
                                                onClick = {
                                                        navController.navigate("register_screen")
                                                }
                                        ) {
                                                Text(
                                                        text = "Sign up",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                )
                                        }
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpBottomSheet(viewModel: AuthViewModel, onDismiss: () -> Unit) {
        val context = LocalContext.current
        val activity = context as? android.app.Activity

        ModalBottomSheet(onDismissRequest = onDismiss) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = if (!viewModel.isOtpSent) "Enter Phone" else "Verify OTP",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!viewModel.isOtpSent) {
                                OutlinedTextField(
                                        value = viewModel.phoneNumber,
                                        onValueChange = { viewModel.phoneNumber = it },
                                        label = { Text("Phone Number (+91...)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions =
                                                KeyboardOptions(keyboardType = KeyboardType.Phone)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                        onClick = {
                                                activity?.let { viewModel.onPhoneLoginClicked(it) }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        enabled =
                                                !viewModel.isLoading &&
                                                        viewModel.phoneNumber.length >= 10,
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF2E7D32)
                                                )
                                ) {
                                        if (viewModel.isLoading)
                                                CircularProgressIndicator(color = Color.White)
                                        else Text("Send Verification Code")
                                }
                        } else {
                                OutlinedTextField(
                                        value = viewModel.otpCode,
                                        onValueChange = {
                                                if (it.length <= 6) viewModel.otpCode = it
                                        },
                                        label = { Text("6-Digit OTP") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.NumberPassword
                                                )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                        onClick = { viewModel.onVerifyOtpClicked() },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        enabled =
                                                !viewModel.isLoading &&
                                                        viewModel.otpCode.length == 6,
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF2E7D32)
                                                )
                                ) {
                                        if (viewModel.isLoading)
                                                CircularProgressIndicator(color = Color.White)
                                        else Text("Verify & Login")
                                }
                        }
                }
        }
}

@Composable
fun SocialIcon(iconId: Int, onClick: () -> Unit = {}) {
        Surface(
                onClick = onClick,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
                color = Color.White
        ) {
                Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = Color.Unspecified
                )
        }
}
