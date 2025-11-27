package com.nathakusuma.neriva.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nathakusuma.neriva.R
import com.nathakusuma.neriva.ui.theme.NerivaTheme

/**
 * Home screen showing user profile, pet information, and quick actions
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel ViewModel for managing home screen state
 * @param onNavigateToChat Callback when user wants to chat with pet
 * @param onNavigateToInbox Callback when user wants to view inbox
 * @param onNavigateToEditProfile Callback when user wants to edit profile
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onNavigateToChat: () -> Unit = {},
    onNavigateToInbox: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val softBeige = Color(0xFFF5EEE7)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(softBeige)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(Modifier.fillMaxSize()) {
                // Top row: avatar, welcome, inbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarUrl = uiState.userProfile?.profilePhoto,
                        onClick = onNavigateToEditProfile
                    )

                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Welcome ${uiState.userProfile?.name ?: "Guest"},",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E1E1E)
                        )
                    }

                    InboxIconWithBadge(
                        countText = "${uiState.userProfile?.unreadMails ?: 0}+",
                        onClick = onNavigateToInbox
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Speech bubble
                SpeechBubble(
                    text = "Feeling tired today? I would like to hear about your story :)"
                )

                Spacer(Modifier.height(16.dp))

                // Illustration
                uiState.pet?.let { pet ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ground ellipse shadow (subtle)
                        Box(
                            modifier = Modifier
                                .width(210.dp)
                                .height(22.dp)
                                .shadow(1.dp, CircleShape)
                                .background(Color(0x22000000), CircleShape)
                        )

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.ic_home_profile_placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Pet Illustration",
                            placeholder = painterResource(id = R.drawable.ic_home_profile_placeholder),
                            error = painterResource(id = R.drawable.ic_home_profile_placeholder),
                            modifier = Modifier.size(240.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Bottom sheet card
                uiState.pet?.let { pet ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                        tonalElevation = 1.dp,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                pet.name,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                pet.animalType,
                                color = Color(0xFF7A7A7A),
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                InfoChip(
                                    title = pet.gender,
                                    subtitle = "Sex",
                                    modifier = Modifier.weight(1f)
                                )
                                InfoChip(
                                    title = pet.birthDate,
                                    subtitle = "Birth",
                                    modifier = Modifier.weight(1f)
                                )
                                InfoChip(
                                    title = "${pet.weight}kg",
                                    subtitle = "Weight",
                                    modifier = Modifier.weight(1f)
                                )
                                InfoChip(
                                    title = if (pet.vaccine) "Yes" else "No",
                                    subtitle = "Vaccine",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(18.dp))

                            Text(
                                "Description:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                pet.description,
                                color = Color(0xFF5E5E5E),
                                lineHeight = 20.sp,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.weight(1f))

                            // CTA button
                            Button(
                                onClick = onNavigateToChat,
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text("Talk to your pet")
                                Spacer(Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.ic_home_paw),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show error if any
        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    modifier: Modifier = Modifier,
    avatarUrl: String?,
    onClick: () -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Profile picture",
        placeholder = painterResource(id = R.drawable.ic_home_profile_placeholder),
        error = painterResource(id = R.drawable.ic_home_profile_placeholder),
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun InboxIconWithBadge(
    modifier: Modifier = Modifier,
    countText: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.size(38.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        // Mail icon
        Surface(
            shape = CircleShape,
            color = Color(0xFFEDE3D7),
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.size(38.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_home_mail),
                    contentDescription = "Inbox"
                )
            }
        }

        // Badge
        if (countText != "0+" && countText != "0") {
            Surface(
                color = Color(0xFFB63B3B),
                shape = CircleShape,
                tonalElevation = 0.dp,
                modifier = Modifier.offset(x = 4.dp, y = (-4).dp)
            ) {
                Text(
                    text = countText,
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                color = Color(0xFF3C3C3C)
            )
        }
        // Tiny tail (bottom-left)
        Canvas(
            modifier = Modifier
                .padding(start = 18.dp)
                .size(18.dp)
                .align(Alignment.BottomStart)
        ) {
            val p = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width, size.height)
                lineTo(0f, 0f)
                close()
            }
            drawPath(p, Color.White)
            drawPath(p, Color(0x11000000), style = Stroke(width = 1f))
        }
    }
}

@Composable
private fun InfoChip(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF5F6F8),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = Color(0xFF8A8A8A), fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    NerivaTheme {
        HomeScreen()
    }
}

