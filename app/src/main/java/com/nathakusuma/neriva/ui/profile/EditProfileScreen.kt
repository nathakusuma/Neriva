package com.nathakusuma.neriva.ui.profile

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nathakusuma.neriva.R
import com.nathakusuma.neriva.ui.theme.NerivaTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private val BackgroundColor = Color(0xFFF3E8DF)
private val PrimaryBrown = Color(0xFF8C6A4F)
private val TextBrown = Color(0xFF6B4F3B)

/**
 * Edit Profile screen for updating user information
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel ViewModel for managing edit profile state
 * @param onBackClick Callback when user clicks back button
 * @param onLogout Callback when user clicks logout button
 */
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showPhotoPickerSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updatePhotoUri(it) }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { viewModel.updatePhotoUri(it) }
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempCameraUri = createImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextBrown
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBrown,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // Dummy box to keep the title centered
                Box(modifier = Modifier.size(48.dp))
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBrown,
                        contentColor = Color.White
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Save changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            ProfilePictureSection(
                profileImageUrl = uiState.profilePhoto,
                selectedPhotoUri = uiState.selectedPhotoUri,
                modifier = Modifier.padding(top = 8.dp),
                onChangePhoto = { showPhotoPickerSheet = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.7f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = PrimaryBrown.copy(alpha = 0.7f),
                        unfocusedBorderColor = PrimaryBrown.copy(alpha = 0.2f),
                        cursorColor = PrimaryBrown,
                        focusedTextColor = TextBrown,
                        unfocusedTextColor = TextBrown
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { }, // No-op since field is read-only
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color.White.copy(alpha = 0.5f),
                        disabledBorderColor = PrimaryBrown.copy(alpha = 0.2f),
                        disabledTextColor = TextBrown.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFD32F2F)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFD32F2F)
                )
            ) {
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Show success message
        uiState.successMessage?.let { message ->
            Snackbar(
                modifier = Modifier
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearMessages() }) {
                        Text("OK")
                    }
                }
            ) {
                Text(message)
            }
        }

        // Show error message
        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearMessages() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(error)
            }
        }
    }

    // Photo Picker Bottom Sheet
    if (showPhotoPickerSheet) {
        PhotoPickerBottomSheet(
            onDismiss = { showPhotoPickerSheet = false },
            onGalleryClick = {
                showPhotoPickerSheet = false
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                showPhotoPickerSheet = false
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }
}

@Composable
private fun ProfilePictureSection(
    profileImageUrl: String?,
    selectedPhotoUri: Uri?,
    onChangePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(150.dp)
                .border(
                    width = 2.dp,
                    color = PrimaryBrown.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .clip(CircleShape),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            // Display selected photo URI if available, otherwise show profile URL
            val imageData = selectedPhotoUri ?: profileImageUrl

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageData)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile picture",
                placeholder = painterResource(id = R.drawable.ic_home_profile_placeholder),
                error = painterResource(id = R.drawable.ic_home_profile_placeholder),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        IconButton(
            onClick = onChangePhoto,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(40.dp)
                .background(PrimaryBrown, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Change profile picture",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoPickerBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Choose Photo",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextBrown,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Gallery Option
            TextButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = BackgroundColor,
                    contentColor = TextBrown
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Photo,
                    contentDescription = "Gallery",
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Choose from Gallery",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Camera Option
            TextButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = BackgroundColor,
                    contentColor = TextBrown
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Camera",
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Take Photo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Helper function to create a temporary URI for camera capture
 */
private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = File(context.getExternalFilesDir(null), "Pictures")

    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val imageFile = File(storageDir, "$imageFileName.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileScreenPreview() {
    NerivaTheme {
        EditProfileScreen()
    }
}
