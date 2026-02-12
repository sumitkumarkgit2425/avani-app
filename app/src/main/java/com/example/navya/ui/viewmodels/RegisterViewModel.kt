package com.example.navya.ui.viewmodels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.arpitkatiyarprojects.countrypicker.utils.CountryPickerUtils.isMobileNumberValid
import com.example.navya.data.repository.AuthRepository
import java.text.SimpleDateFormat
import java.util.*

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var registrationError by mutableStateOf<String?>(null)
    var isRegistrationSuccessful by mutableStateOf(false)

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var birthDate by mutableStateOf("dd/mm/yy")
    var password by mutableStateOf("")

    var isPasswordVisible by mutableStateOf(false)

    var phoneNumber by mutableStateOf("")
    var selectedCountryCode by mutableStateOf("+91")
    var selectedCountry by mutableStateOf<CountryDetails?>(null)

    var showDatePicker by mutableStateOf(false)

    var phoneError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)

    fun validateForm(): Boolean {
        var isValid = true

        val currentCountryName = selectedCountry?.countryName ?: "Selected Country"

        if (selectedCountry == null || !isMobileNumberValid(phoneNumber, selectedCountry?.countryCode ?: "")) {
            phoneError = "Invalid phone number for $currentCountryName"
            isValid = false
        } else {
            phoneError = null
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        return isValid
    }

    fun updateDate(millis: Long?) {
        millis?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            birthDate = formatter.format(Date(it))
        }
    }
    fun onRegisterClicked() {
        if (validateForm()) {
            isLoading = true
            registrationError = null

            repository.registerWithEmail(email, password) { success, error ->
                isLoading = false
                if (success) {
                    isRegistrationSuccessful = true //
                } else {
                    registrationError = error //
                }
            }
        }
    }
}