package com.example.navya.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.arpitkatiyarprojects.countrypicker.CountryPickerOutlinedTextField
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.example.navya.R
import com.example.navya.ui.components.NavyaTextField
import com.example.navya.ui.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
        navController: NavHostController,
        viewModel: RegisterViewModel = hiltViewModel()
) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val datePickerState = rememberDatePickerState()


        LaunchedEffect(viewModel.isRegistrationSuccessful) {
                if (viewModel.isRegistrationSuccessful) {
                        navController.navigate("home_screen") {
                                popUpTo("register_screen") { inclusive = true }
                                popUpTo("login_screen") { inclusive = true }
                        }
                }
        }

        LaunchedEffect(viewModel.registrationError) {
                viewModel.registrationError?.let { error ->
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

        if (viewModel.showDatePicker) {
                DatePickerDialog(
                        onDismissRequest = { viewModel.showDatePicker = false },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                viewModel.updateDate(
                                                        datePickerState.selectedDateMillis
                                                )
                                                viewModel.showDatePicker = false
                                        }
                                ) { Text("OK") }
                        },
                        dismissButton = {
                                TextButton(onClick = { viewModel.showDatePicker = false }) {
                                        Text("Cancel")
                                }
                        }
                ) { DatePicker(state = datePickerState) }
        }

        Box(
                modifier =
                        Modifier.fillMaxSize().background(fullPageGradient).pointerInput(Unit) {
                                detectTapGestures(onTap = { focusManager.clearFocus() })
                        }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = Color.White
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                                Column(
                                        modifier = Modifier.padding(24.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        Row(
                                                modifier =
                                                        Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                                Icon(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.avanipng
                                                                ),
                                                        contentDescription = null,
                                                        tint = Color.Unspecified,
                                                        modifier = Modifier.size(50.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        text = "Sign Up",
                                                        fontSize = 32.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                )
                                        }

                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier =
                                                        Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                                Text(
                                                        "Already have an account? ",
                                                        fontSize = 14.sp,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Text(
                                                        text = "Log In",
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier =
                                                                Modifier.clickable {
                                                                        navController.navigate(
                                                                                "login_screen"
                                                                        )
                                                                }
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                NavyaTextField(
                                                        value = viewModel.firstName,
                                                        onValueChange = {
                                                                viewModel.firstName = it
                                                        },
                                                        label = "First Name",
                                                        modifier = Modifier.weight(1f),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        imeAction = ImeAction.Next
                                                                )
                                                )
                                                NavyaTextField(
                                                        value = viewModel.lastName,
                                                        onValueChange = { viewModel.lastName = it },
                                                        label = "Last Name",
                                                        modifier = Modifier.weight(1f),
                                                        singleLine = true,
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        imeAction = ImeAction.Next
                                                                )
                                                )
                                        }

                                        NavyaTextField(
                                                value = viewModel.email,
                                                onValueChange = { viewModel.email = it },
                                                label = "Email",
                                                isError = viewModel.emailError != null,
                                                errorMessage = viewModel.emailError,
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Email,
                                                                imeAction = ImeAction.Next
                                                        )
                                        )

                                        NavyaTextField(
                                                value = viewModel.birthDate,
                                                onValueChange = {},
                                                label = "Date Of Birth",
                                                modifier = Modifier.fillMaxWidth(),
                                                readOnly = true,
                                                trailingIcon = {
                                                        IconButton(
                                                                onClick = {
                                                                        viewModel.showDatePicker =
                                                                                true
                                                                }
                                                        ) {
                                                                Icon(
                                                                        Icons.Default.CalendarMonth,
                                                                        contentDescription =
                                                                                "Pick Date",
                                                                        tint =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                )
                                                        }
                                                },
                                        )

                                        CountryPickerOutlinedTextField(
                                                mobileNumber = viewModel.phoneNumber,
                                                onMobileNumberChange = {
                                                        viewModel.phoneNumber = it
                                                },
                                                onCountrySelected = { country: CountryDetails ->
                                                        viewModel.selectedCountry = country
                                                        viewModel.selectedCountryCode =
                                                                country.countryPhoneNumberCode
                                                },
                                                onDone = { focusManager.clearFocus() },
                                                label = { Text("Phone Number") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                isError = viewModel.phoneError != null,
                                                supportingText = {
                                                        viewModel.phoneError?.let { Text(it) }
                                                },
                                                singleLine = true,
                                                isPickerEnabled = true,
                                        )

                                        NavyaTextField(
                                                value = viewModel.password,
                                                onValueChange = { viewModel.password = it },
                                                label = "Set Password",
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true,
                                                visualTransformation =
                                                        if (viewModel.isPasswordVisible)
                                                                VisualTransformation.None
                                                        else PasswordVisualTransformation(),
                                                trailingIcon = {
                                                        val image =
                                                                if (viewModel.isPasswordVisible)
                                                                        Icons.Default.Visibility
                                                                else Icons.Default.VisibilityOff
                                                        IconButton(
                                                                onClick = {
                                                                        viewModel
                                                                                .isPasswordVisible =
                                                                                !viewModel
                                                                                        .isPasswordVisible
                                                                }
                                                        ) {
                                                                Icon(
                                                                        image,
                                                                        contentDescription = null,
                                                                        tint = Color.Gray
                                                                )
                                                        }
                                                }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                                onClick = { viewModel.onRegisterClicked() },
                                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                                enabled = !viewModel.isLoading,
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
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
                                                                "Register",
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                }
                                        }
                                }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                }
        }
}
