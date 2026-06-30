package urstark.solstice.ui.screens.social

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import org.matrix.rustcomponents.sdk.CreateRoomParameters
import org.matrix.rustcomponents.sdk.RoomPreset
import org.matrix.rustcomponents.sdk.RoomVisibility
import org.matrix.rustcomponents.sdk.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var showJoinAddressDialog by remember { mutableStateOf(false) }
    var joinAddress by remember { mutableStateOf("") }
    var isJoiningAddress by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Start Chat") },
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
            // Search Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search for someone by Matrix ID") },
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (searchQuery.isNotBlank()) {
                                isSearching = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val client = viewModel.matrixManager.client
                                        if (client != null) {
                                            val results = client.searchUsers(searchQuery, 10uL)
                                            searchResults = results.results
                                            if (searchResults.isEmpty()) {
                                                errorMessage = "No users found"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to search: ${e.message}"
                                    } finally {
                                        isSearching = false
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isBlank()) {
                // Main Options
                Column(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("New Room", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Create a private or public room") },
                        leadingContent = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        },
                        modifier = Modifier.clickable { navController.navigate("create_room") }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("New Space", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Organize rooms and members") },
                        leadingContent = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.GroupAdd, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        },
                        modifier = Modifier.clickable { navController.navigate("create_space") }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Room Directory", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Explore public rooms on the homeserver") },
                        leadingContent = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Explore, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        },
                        modifier = Modifier.clickable { navController.navigate("room_directory") }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Join Room by Address", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Join a room using its alias or ID") },
                        leadingContent = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        },
                        modifier = Modifier.clickable { showJoinAddressDialog = true }
                    )
                    HorizontalDivider()
                }
            } else {
                // Search Results
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(searchResults) { profile ->
                            ListItem(
                                headlineContent = { Text(profile.displayName ?: profile.userId) },
                                supportingContent = { Text(profile.userId) },
                                leadingContent = {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                                },
                                modifier = Modifier.clickable {
                                    isSearching = true
                                    scope.launch {
                                        try {
                                            val client = viewModel.matrixManager.client
                                            if (client != null) {
                                                val params = CreateRoomParameters(
                                                    name = null,
                                                    topic = null,
                                                    isEncrypted = true,
                                                    isDirect = true,
                                                    visibility = RoomVisibility.PRIVATE,
                                                    preset = RoomPreset.PRIVATE_CHAT,
                                                    invite = listOf(profile.userId),
                                                    avatar = null,
                                                    powerLevelContentOverride = null
                                                )
                                                val roomId = client.createRoom(params)
                                                navController.navigate("chat/$roomId") {
                                                    popUpTo("social")
                                                }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Failed to start chat: ${e.message}"
                                            isSearching = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showJoinAddressDialog) {
        AlertDialog(
            onDismissRequest = { if (!isJoiningAddress) showJoinAddressDialog = false },
            title = { Text("Join Room by Address") },
            text = {
                Column {
                    Text("Enter the address or ID of the room you want to join (e.g. #room_name:matrix.org or !room_id:matrix.org):")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = joinAddress,
                        onValueChange = { joinAddress = it },
                        label = { Text("Room Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isJoiningAddress
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (joinAddress.isNotBlank()) {
                            isJoiningAddress = true
                            scope.launch {
                                try {
                                    val success = viewModel.joinRoom(joinAddress)
                                    if (success) {
                                        android.widget.Toast.makeText(context, "Joined successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                        showJoinAddressDialog = false
                                        // Find the room id to navigate, or we can just popBackStack since they joined
                                        navController.popBackStack()
                                    } else {
                                        android.widget.Toast.makeText(context, "Failed to join room", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Error: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                } finally {
                                    isJoiningAddress = false
                                }
                            }
                        }
                    },
                    enabled = !isJoiningAddress && joinAddress.isNotBlank()
                ) {
                    if (isJoiningAddress) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Join")
                    }
                }
            },
            dismissButton = {
                if (!isJoiningAddress) {
                    TextButton(onClick = { showJoinAddressDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
