package com.nathakusuma.neriva.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nathakusuma.neriva.data.model.ChatMessage
import com.nathakusuma.neriva.utils.ChatUtils
import com.nathakusuma.neriva.utils.DateTimeUtils

/**
 * Chat screen for communicating with the pet
 *
 * @param modifier Modifier to be applied to the root composable
 * @param viewModel ViewModel for managing chat state
 * @param onBackClick Callback when back button is clicked
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = run {
        val context = LocalContext.current
        viewModel(factory = ChatViewModelFactory(context))
    },
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val chatBackground = Color(0xFFF5E9E2)
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Track if initial load is complete to prevent premature pagination
    var initialLoadComplete by remember { mutableStateOf(false) }

    // Mark initial load as complete once we have messages and not loading
    LaunchedEffect(uiState.messages.isNotEmpty(), uiState.isLoading) {
        if (uiState.messages.isNotEmpty() && !uiState.isLoading) {
            initialLoadComplete = true
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    // Load more messages when scrolling to top
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleIndex ->
                // Only load more after initial load is complete, when user scrolls near the top
                if (initialLoadComplete && firstVisibleIndex == 0 && uiState.canLoadMore && !uiState.isLoadingMore && !uiState.isLoading) {
                    viewModel.loadMoreMessages()
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                name = uiState.pet?.name ?: "Pet",
                avatarUrl = uiState.pet?.petProfileImage ?: "",
                isActive = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ChatInputBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(chatBackground),
                onSendMessage = { message ->
                    viewModel.sendMessage(message)
                },
                enabled = !uiState.isSending
            )
        },
        containerColor = chatBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
                ) {
                    item {
                        DayHeader(label = "Today")
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    itemsIndexed(uiState.messages, key = { _, msg -> msg.id }) { index, message ->
                        val showAuthorName =
                            !message.fromMe && (index == 0 || uiState.messages[index - 1].fromMe)

                        ChatMessageRow(
                            message = message,
                            showAuthorName = showAuthorName,
                            avatarUrl = uiState.pet?.petProfileImage ?: "",
                            petName = uiState.pet?.name
                        )

                        Spacer(modifier = Modifier.height(12.dp))
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
}

@Composable
fun ChatTopBar(
    name: String,
    avatarUrl: String,
    isActive: Boolean,
    onBackClick: () -> Unit
) {
    Surface(
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            ProfileAvatar(
                avatarUrl = avatarUrl,
                isActive = isActive,
                size = 44.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = if (isActive) "Active now" else "Offline",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    avatarUrl: String,
    isActive: Boolean,
    size: Dp
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.BottomEnd
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(size * 0.33f)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
                    .align(Alignment.BottomEnd)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun DayHeader(label: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.9f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF8B7C70),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChatMessageRow(
    message: ChatMessage,
    showAuthorName: Boolean,
    avatarUrl: String,
    petName: String? = null
) {
    val myBubbleColor = Color(0xFF7B5A48)
    val friendBubbleColor = Color.White
    val timeColor = Color(0xFFB1A79E)

    val formattedTime = DateTimeUtils.formatChatTime(message.createdAt)
    val authorName = ChatUtils.getAuthorName(message.senderType, petName)

    if (message.fromMe) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    color = myBubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 4.dp
                    )
                ) {
                    Text(
                        text = message.message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = timeColor
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            ProfileAvatar(
                avatarUrl = avatarUrl,
                isActive = true,
                size = 40.dp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                if (showAuthorName) {
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF6D5E54)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Surface(
                    color = friendBubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                ) {
                    Text(
                        text = message.message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color(0xFF4B3C34),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = timeColor
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit,
    enabled: Boolean = true
) {
    var input by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .weight(1f)
                    .shadow(1.dp, RoundedCornerShape(32.dp)),
                placeholder = { Text("Write your message") },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                enabled = enabled
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = Color(0xFF7B5A48)
            ) {
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            onSendMessage(input.trim())
                            input = ""
                        }
                    },
                    enabled = enabled && input.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
