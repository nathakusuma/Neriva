package com.nathakusuma.neriva.ui.onboarding

import com.nathakusuma.neriva.R

/**
 * Data class representing an onboarding page
 *
 * @param title The title text to display
 * @param description The description text to display
 * @param illustrationRes Drawable resource ID for the illustration
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val illustrationRes: Int
)

/**
 * Sample onboarding pages data
 */
object OnboardingData {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Aviren!",
            description = "We're here to help you heal from loneliness and isolation, supporting your mental health recovery",
            illustrationRes = R.drawable.ic_onboarding_1
        ),
        OnboardingPage(
            title = "We Help You Here",
            description = "Our platform make you comfort to tackle the challenges of depression and suicide among younger generations",
            illustrationRes = R.drawable.ic_onboarding_2

        )
    )
}
