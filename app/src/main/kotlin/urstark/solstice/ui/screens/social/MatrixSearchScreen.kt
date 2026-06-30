package urstark.solstice.ui.screens.social

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import urstark.solstice.matrix.PublicRoomChunk
import urstark.solstice.matrix.UserSearchResult
import org.matrix.rustcomponents.sdk.CreateRoomParameters
import org.matrix.rustcomponents.sdk.RoomPreset
import org.matrix.rustcomponents.sdk.RoomVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixSearchScreen(navController: NavController, viewModel: SocialViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Public Spaces", "Public Rooms", "People", "Messages")
    val scope = rememberCoroutineScope()
    
    var publicRooms by remember { mutableStateOf<List<PublicRoomChunk>>(emptyList()) }
    var peopleResults by remember { mutableStateOf<List<UserSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery, selectedTab) {
        if (searchQuery.isNotBlank() && (selectedTab == 0 || selectedTab == 1 || selectedTab == 2)) {
            isSearching = true
            try {
                if (selectedTab == 0 || selectedTab == 1) {
                    val results = viewModel.searchPublicRooms(searchQuery, 50)
                    publicRooms = if (selectedTab == 0) results else results
                } else if (selectedTab == 2) {
                    peopleResults = viewModel.searchUsers(searchQuery, 50)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSearching = false
            }
        } else {
            publicRooms = emptyList()
            peopleResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (actionError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        text = actionError!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (searchQuery.isBlank()) {
                    Text("Type to search in ${tabs[selectedTab]}")
                } else if (isSearching) {
                    CircularProgressIndicator()
                } else {
                    if (selectedTab == 0 || selectedTab == 1) {
                        if (publicRooms.isEmpty()) {
                            Text("No results found for '$searchQuery'")
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(publicRooms) { room ->
                                    ListItem(
                                        headlineContent = { Text(room.name ?: room.canonical_alias ?: room.room_id) },
                                        supportingContent = { Text(room.topic ?: "${room.num_joined_members} members") },
                                        modifier = Modifier.clickable {
                                            isSearching = true
                                            actionError = null
                                            scope.launch {
                                                try {
                                                    val success = viewModel.joinRoom(room.room_id)
                                                    if (success) {
                                                        navController.navigate("chat/${room.room_id}") {
                                                            popUpTo("social")
                                                        }
                                                    } else {
                                                        actionError = "Failed to join room"
                                                    }
                                                } catch (e: Exception) {
                                                    actionError = "Failed to join room: ${e.message}"
                                                } finally {
                                                    isSearching = false
                                                }
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    } else if (selectedTab == 2) {
                        if (peopleResults.isEmpty()) {
                            Text("No results found for '$searchQuery'")
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(peopleResults) { person ->
                                    ListItem(
                                        headlineContent = { Text(person.display_name ?: person.user_id) },
                                        supportingContent = { Text(person.user_id) },
                                        modifier = Modifier.clickable {
                                            isSearching = true
                                            actionError = null
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
                                                            invite = listOf(person.user_id),
                                                            avatar = null,
                                                            powerLevelContentOverride = null
                                                        )
                                                        val roomId = client.createRoom(params)
                                                        navController.navigate("chat/$roomId") {
                                                            popUpTo("social")
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    actionError = "Failed to start chat: ${e.message}"
                                                } finally {
                                                    isSearching = false
                                                }
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    } else {
                        // Messages tabs not fully supported by simple REST API
                        Text("Search for '${tabs[selectedTab]}' not implemented yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
