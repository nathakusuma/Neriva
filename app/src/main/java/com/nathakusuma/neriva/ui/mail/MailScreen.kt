package com.nathakusuma.neriva.ui.mail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathakusuma.neriva.data.model.Mail
import com.nathakusuma.neriva.ui.theme.NerivaTheme

/**
 * Mail screen showing list of messages
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel ViewModel for managing mail screen state
 * @param onBackClick Callback when user wants to navigate back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailScreen(
    modifier: Modifier = Modifier,
    viewModel: MailViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val uiState by viewModel.uiState.collectAsState()

    var selectedMail by remember { mutableStateOf<Mail?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7EBDD)) // light beige background
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Mail",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFFBF5)
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.mails, key = { it.id }) { mail ->
                        MailCard(
                            mail = mail,
                            onClick = {
                                selectedMail = mail
                            },
                            onDeleteClick = {
                                viewModel.deleteMail(mail.id)
                            }
                        )
                    }
                }
            }
        }

        if (selectedMail != null) {
            MailDetailDialog(
                mail = selectedMail!!,
                onDismiss = { selectedMail = null }
            )
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

/**
 * Card displaying mail preview
 *
 * @param mail Mail data to display
 * @param onClick Callback when card is clicked
 * @param onDeleteClick Callback when delete icon is clicked
 */
@Composable
fun MailCard(
    mail: Mail,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8D6C62) // warm brown
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(end = 32.dp) // space for delete icon
            ) {
                Text(
                    text = mail.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFF3E2)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mail.preview,
                    fontSize = 14.sp,
                    color = Color(0xFFFBE9D7),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF4B4B)
                )
            }
        }
    }
}

/**
 * Dialog showing full mail content
 *
 * @param mail Mail data to display
 * @param onDismiss Callback when dialog is dismissed
 */
@Composable
fun MailDetailDialog(
    mail: Mail,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)) // dim background
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFBF5)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        enabled = false,
                        onClick = { }
                    ) // prevent click-through
            ) {
                Text(
                    text = mail.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A4638)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mail.body,
                    fontSize = 16.sp,
                    color = Color(0xFF5A4638),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MailScreenPreview() {
    NerivaTheme {
        MailScreen()
    }
}

