package com.example.navya.data.models

import com.example.navya.R

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        "Discover Your Green Space",
        "Explore our shop UI to find the perfect indoor plants for your home.",
        R.drawable.onboarding_discover
    ),
    OnboardingPage(
        "Perfect Light, Every Time",
        "Use your phone's sensor as a light meter to check if your plants are happy.",
        R.drawable.onboarding_light
    ),
    OnboardingPage(
        "Nurture with Ease",
        "Get smart background reminders so you never forget to water your plants.",
        R.drawable.onboarding_nurture
    )
)