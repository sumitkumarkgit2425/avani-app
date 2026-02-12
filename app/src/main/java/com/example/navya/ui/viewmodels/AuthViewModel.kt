package com.example.navya.ui.viewmodels

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.navya.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var emailText by mutableStateOf("")
    var passwordText by mutableStateOf("")
    var isRemembered by mutableStateOf(false)
    var isPasswordVisible by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var isLoginSuccessful by mutableStateOf(false)

    fun onLoginClicked() {
        if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
            isLoading = true
            loginError = null

            repository.loginWithEmail(emailText, passwordText) { success, error ->
                isLoading = false
                if (success) {
                    isLoginSuccessful = true
                } else {
                    loginError = error
                }
            }
        } else {
            loginError = "Email and Password cannot be empty"
        }
    }
    var phoneNumber by mutableStateOf("")
    var otpCode by mutableStateOf("")
    var showOtpSheet by mutableStateOf(false)

    var isOtpSent by mutableStateOf(false)

    fun onPhoneIconClicked() {
        showOtpSheet = true
        isOtpSent = false
    }

    var showOtpDialog by mutableStateOf(false)
    private var verificationId: String = ""

    fun onPhoneLoginRequested(activity: Activity) {
        isLoading = true
        repository.sendOtp(phoneNumber, activity) { error ->
            isLoading = false
            if (error == null) {
                showOtpDialog = true
            } else {
                loginError = error
            }
        }
    }
    fun onPhoneLoginClicked(activity: android.app.Activity) {
        val formattedNumber = if (phoneNumber.startsWith("+")) {
            phoneNumber
        } else {
            "+91$phoneNumber"
        }

        if (formattedNumber.length >= 13) {
            isLoading = true
            loginError = null

            repository.sendOtp(formattedNumber, activity) { error ->
                isLoading = false
                if (error == null) {
                    isOtpSent = true
                } else {
                    loginError = error
                }
            }
        } else {
            loginError = "Please enter a valid 10-digit number"
        }
    }
    fun onVerifyOtpClicked() {
        isLoading = true
        repository.verifyOtp(otpCode) { success, error ->
            isLoading = false
            if (success) {
                isLoginSuccessful = true
                showOtpDialog = false
            } else {
                loginError = error
            }
        }
    }
    var showPhoneInputDialog by mutableStateOf(false)

    fun loginWithGoogle(idToken: String) {
        isLoading = true
        loginError = null

        repository.signInWithGoogle(idToken) { success, error ->
            isLoading = false
            if (success) {
                isLoginSuccessful = true
            } else {
                loginError = error
            }
        }
    }
}