package com.nathakusuma.neriva.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathakusuma.neriva.data.local.TokenManager

/**
 * Onboarding screen for introducing users to the app
 *
 * @param modifier Modifier to be applied to the root composable
 * @param pages List of onboarding pages to display
 * @param viewModel The ViewModel that manages onboarding state
 * @param onFinish Callback when onboarding is finished (last page next button)
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = OnboardingData.pages,
    viewModel: OnboardingViewModel = viewModel(),
    onFinish: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokenManager = remember { TokenManager.getInstance() }

    OnboardingScreenContent(
        modifier = modifier,
        currentPage = pages[uiState.currentPageIndex],
        currentPageIndex = uiState.currentPageIndex,
        totalPages = pages.size,
        isFirstPage = uiState.isFirstPage,
        isLastPage = uiState.isLastPage,
        onNavigateBack = viewModel::navigateBack,
        onNavigateNext = {
            if (!uiState.isLastPage) {
                viewModel.navigateNext()
            } else {
                // Mark onboarding as completed before finishing
                tokenManager.setOnboardingCompleted()
                onFinish()
            }
        }
    )
}

// Constants
private object OnboardingConstants {
    const val BUTTON_TEXT_BACK = "Back"
    const val BUTTON_TEXT_NEXT = "Next"
    const val BUTTON_TEXT_GET_STARTED = "Get Started"
}

/**
 * Onboarding screen content displaying a single page with navigation
 */
@Composable
private fun OnboardingScreenContent(
    modifier: Modifier = Modifier,
    currentPage: OnboardingPage,
    currentPageIndex: Int,
    totalPages: Int,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            OnboardingNavigationBar(
                isFirstPage = isFirstPage,
                isLastPage = isLastPage,
                onNavigateBack = onNavigateBack,
                onNavigateNext = onNavigateNext
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            OnboardingIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .aspectRatio(0.9f),
                illustrationRes = currentPage.illustrationRes
            )

            Spacer(Modifier.height(8.dp))

            OnboardingTitle(
                title = currentPage.title,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OnboardingDescription(
                description = currentPage.description,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            PageIndicator(
                totalPages = totalPages,
                currentPageIndex = currentPageIndex
            )

            Spacer(Modifier.weight(1f))
        }
    }
}

/**
 * Navigation bar for onboarding with back and next buttons
 */
@Composable
private fun OnboardingNavigationBar(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = if (isFirstPage) Arrangement.End else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isFirstPage) {
            OutlinedButton(onClick = onNavigateBack) {
                Text(OnboardingConstants.BUTTON_TEXT_BACK)
            }
        }

        Button(onClick = onNavigateNext) {
            Text(
                text = if (isLastPage) {
                    OnboardingConstants.BUTTON_TEXT_GET_STARTED
                } else {
                    OnboardingConstants.BUTTON_TEXT_NEXT
                }
            )
        }
    }
}

/**
 * Displays the onboarding page title
 */
@Composable
private fun OnboardingTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

/**
 * Displays the onboarding page description
 */
@Composable
private fun OnboardingDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

/**
 * Displays the onboarding illustration image
 */
@Composable
private fun OnboardingIllustration(
    illustrationRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = illustrationRes),
        contentDescription = "Onboarding illustration",
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

/**
 * Page indicator showing dots for each page
 */
@Composable
private fun PageIndicator(
    totalPages: Int,
    currentPageIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 8.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(totalPages) { index ->
            val color = if (index == currentPageIndex) selectedColor else unselectedColor
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
            )
            if (index != totalPages - 1) Spacer(Modifier.width(dotSpacing))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingScreenContentPreview() {
    MaterialTheme {
        OnboardingScreenContent(
            currentPage = OnboardingData.pages[1],
            currentPageIndex = 1,
            totalPages = 2,
            isFirstPage = false,
            isLastPage = true,
            onNavigateBack = {},
            onNavigateNext = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingScreenPreview() {
    MaterialTheme {
        OnboardingScreen(
            onFinish = {}
        )
    }
}
