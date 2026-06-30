package urstark.solstice.ui.screens.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import urstark.solstice.matrix.PublicRoomChunk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDirectoryScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf("") }
    var roomsList by remember { mutableStateOf<List<PublicRoomChunk>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch default public rooms on start
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            roomsList = viewModel.searchPublicRooms("", 50)
        } catch (e: Exception) {
            errorMessage = "Failed to load directory: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    fun doSearch() {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                roomsList = viewModel.searchPublicRooms(searchQuery, 50)
                if (roomsList.isEmpty()) {
                    errorMessage = "No public rooms found"
                }
            } catch (e: Exception) {
                errorMessage = "Search failed: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room Directory") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
        ) {
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search public rooms") },
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { doSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(roomsList) { room ->
                        var isJoining by remember { mutableStateOf(false) }
                        ListItem(
                            headlineContent = { Text(room.name ?: room.canonical_alias ?: "Unnamed Room") },
                            supportingContent = {
                                Column {
                                    if (!room.topic.isNullOrBlank()) {
                                        Text(room.topic, maxLines = 2)
                                    }
                                    Text(
                                        text = "${room.canonical_alias ?: room.room_id} • ${room.num_joined_members} members",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            leadingContent = {
                                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(40.dp))
                            },
                            trailingContent = {
                                Button(
                                    onClick = {
                                        isJoining = true
                                        scope.launch {
                                            try {
                                                val success = viewModel.joinRoom(room.room_id)
                                                if (success) {
                                                    android.widget.Toast.makeText(context, "Joined successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                                    navController.navigate("chat/${room.room_id}") {
                                                        popUpTo("social")
                                                    }
                                                } else {
                                                    android.widget.Toast.makeText(context, "Failed to join", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                android.widget.Toast.makeText(context, "Error: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isJoining = false
                                            }
                                        }
                                    },
                                    enabled = !isJoining
                                ) {
                                    if (isJoining) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    } else {
                                        Text("Join")
                                    }
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
