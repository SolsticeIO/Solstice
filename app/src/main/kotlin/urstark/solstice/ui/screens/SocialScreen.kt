/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
package urstark.solstice.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.unit.sp
import urstark.solstice.matrix.MatrixRoomListManager.RoomInfo
import urstark.solstice.ui.screens.social.SocialLoginScreen
import urstark.solstice.ui.screens.social.SocialViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import urstark.solstice.ui.component.ExpressivePullToRefreshBox

import org.matrix.rustcomponents.sdk.RecoveryState
import org.matrix.rustcomponents.sdk.EnableRecoveryProgressListener
import org.matrix.rustcomponents.sdk.EnableRecoveryProgress
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

enum class ChatFilter(val title: String) {
    ALL("All"),
    UNREAD("Unread"),
    PEOPLE("People"),
    ROOMS("Rooms"),
    FAVOURITES("Favourites"),
    INVITES("Invites")
}

@Composable
fun SocialScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.matrixManager.isLoggedIn.collectAsState()
    val isSessionVerified by viewModel.isSessionVerified.collectAsState()
    val context = LocalContext.current
    
    val isScreenLockEnabled by viewModel.isScreenLockEnabled.collectAsState()
    var isScreenUnlocked by remember { mutableStateOf(!isScreenLockEnabled) }
    
    var showRecoverySetup by remember { mutableStateOf(false) }
    var showRecoveryPrompt by remember { mutableStateOf(false) }
    var recoveryPassphrase by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isLoggedIn, isScreenLockEnabled, isSessionVerified) {
        if (isLoggedIn) {
            viewModel.loadRooms()
            viewModel.loadSettingsData()
            if (isSessionVerified) {
                val encryption = viewModel.matrixManager.client?.encryption()
                val state = encryption?.recoveryState()
                if (state == RecoveryState.DISABLED) {
                    showRecoverySetup = true
                } else if (state == RecoveryState.INCOMPLETE) {
                    showRecoveryPrompt = true
                }
            }
            
            if (isScreenLockEnabled && !isScreenUnlocked) {
                val activity = context as? android.app.Activity
                if (activity != null) {
                    try {
                        val biometricPrompt = android.hardware.biometrics.BiometricPrompt.Builder(context)
                            .setTitle("Unlock Solstice Social")
                            .setSubtitle("Confirm your identity to access chats")
                            .setNegativeButton("Cancel", context.mainExecutor) { _, _ -> }
                            .build()
                            
                        biometricPrompt.authenticate(
                            android.os.CancellationSignal(),
                            context.mainExecutor,
                            object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) {
                                    super.onAuthenticationSucceeded(result)
                                    isScreenUnlocked = true
                                }
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isScreenUnlocked = true
                    }
                } else {
                    isScreenUnlocked = true
                }
            }
        }
    }

    if (!isLoggedIn) {
        SocialLoginScreen(matrixManager = viewModel.matrixManager)
    } else if (isScreenLockEnabled && !isScreenUnlocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Block, // using Block as a lock-like icon or we can use another one
                    contentDescription = "Locked",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Solstice Social is Locked", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        try {
                            val biometricPrompt = android.hardware.biometrics.BiometricPrompt.Builder(context)
                                .setTitle("Unlock Solstice Social")
                                .setSubtitle("Confirm your identity to access chats")
                                .setNegativeButton("Cancel", context.mainExecutor) { _, _ -> }
                                .build()
                                
                            biometricPrompt.authenticate(
                                android.os.CancellationSignal(),
                                context.mainExecutor,
                                object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                                    override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) {
                                        super.onAuthenticationSucceeded(result)
                                        isScreenUnlocked = true
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            isScreenUnlocked = true
                        }
                    }
                }) {
                    Text("Unlock with Biometrics")
                }
            }
        }
    } else {
        SocialMainContent(navController, viewModel)
        
        // Security Warning: Setup Recovery
        if (showRecoverySetup) {
            var isEnablingRecovery by remember { mutableStateOf(false) }
            AlertDialog(
                onDismissRequest = { if (!isEnablingRecovery) showRecoverySetup = false },
                title = { Text("⚠️ Secure Backup Not Enabled!") },
                text = { Text("Your chats are End-to-End Encrypted. If you lose this device without enabling Secure Backup, you will PERMANENTLY lose access to all your past messages. Please enable it now.") },
                confirmButton = {
                    Button(onClick = {
                        isEnablingRecovery = true
                        val encryption = viewModel.matrixManager.client?.encryption()
                        if (encryption != null) {
                            scope.launch {
                                try {
                                    val passphrase = encryption.enableRecovery(false, object : EnableRecoveryProgressListener {
                                        override fun onUpdate(status: EnableRecoveryProgress) {}
                                    })
                                    recoveryPassphrase = passphrase
                                    showRecoverySetup = false
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Failed: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                } finally {
                                    isEnablingRecovery = false
                                }
                            }
                        }
                    }) {
                        if (isEnablingRecovery) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text("Enable Secure Backup")
                    }
                },
                dismissButton = {
                    if (!isEnablingRecovery) {
                        TextButton(onClick = { showRecoverySetup = false }) {
                            Text("Remind Me Later")
                        }
                    }
                }
            )
        }
        
        // Show Passphrase after setup
        if (recoveryPassphrase != null) {
            val clipboard = LocalClipboardManager.current
            AlertDialog(
                onDismissRequest = { recoveryPassphrase = null },
                title = { Text("🚨 SAVE THIS PASSPHRASE 🚨") },
                text = { 
                    Column {
                        Text("This is your ONLY way to recover your messages if you log out or lose this device. Store it somewhere safe like a password manager. Matrix cannot recover this for you!", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                            Text(recoveryPassphrase!!, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        clipboard.setText(buildAnnotatedString { append(recoveryPassphrase!!) })
                        recoveryPassphrase = null
                    }) {
                        Text("Copy & Close")
                    }
                }
            )
        }
        
        // Recovery Prompt for existing users logging in
        if (showRecoveryPrompt) {
            var inputPassphrase by remember { mutableStateOf("") }
            var isRecovering by remember { mutableStateOf(false) }
            
            AlertDialog(
                onDismissRequest = { /* Force user to dismiss via button or keep on screen */ },
                title = { Text("Unlock Chat History") },
                text = { 
                    Column {
                        Text("Welcome back! Your chat history is encrypted. Please enter your Recovery Passphrase to unlock your messages.")
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = inputPassphrase,
                            onValueChange = { inputPassphrase = it },
                            label = { Text("Recovery Passphrase") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isRecovering = true
                            scope.launch {
                                try {
                                    viewModel.matrixManager.client?.encryption()?.recover(inputPassphrase)
                                    showRecoveryPrompt = false
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    isRecovering = false
                                }
                            }
                        },
                        enabled = !isRecovering && inputPassphrase.isNotBlank()
                    ) {
                        Text(if (isRecovering) "Unlocking..." else "Unlock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRecoveryPrompt = false }) {
                        Text("Skip")
                    }
                }
            )
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .height(38.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMainContent(navController: NavController, viewModel: SocialViewModel) {
    var selectedFilter by remember { mutableStateOf(ChatFilter.ALL) }
    
    val rooms by viewModel.roomListManager.rooms.collectAsState()
    val isLoading by viewModel.roomListManager.isLoading.collectAsState()
    val isSessionVerified by viewModel.isSessionVerified.collectAsState()
    
    var showVerificationSheet by remember { mutableStateOf(false) }
    
    val filteredRooms = remember(rooms, selectedFilter) {
        when (selectedFilter) {
            ChatFilter.ALL -> rooms.filter { !it.isInvite }
            ChatFilter.UNREAD -> rooms.filter { !it.isInvite && (it.unreadCount > 0 || it.highlightCount > 0 || it.isMarkedUnread) }
            ChatFilter.PEOPLE -> rooms.filter { !it.isInvite && it.isDirect }
            ChatFilter.ROOMS -> rooms.filter { !it.isInvite && !it.isDirect }
            ChatFilter.FAVOURITES -> rooms.filter { !it.isInvite && it.isPinned }
            ChatFilter.INVITES -> rooms.filter { it.isInvite }
        }
    }
    
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp
    
    var selectedRoomForOptions by remember { mutableStateOf<RoomInfo?>(null) }

    var viewingUserId by remember { mutableStateOf<String?>(null) }
    var viewingProfileName by remember { mutableStateOf<String?>(null) }
    var showGroupInfo by remember { mutableStateOf(false) }
    var selectedGroupRoomId by remember { mutableStateOf<String?>(null) }
    var selectedGroupName by remember { mutableStateOf<String?>(null) }
    var roomMembers by remember { mutableStateOf<List<org.matrix.rustcomponents.sdk.RoomMember>?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("new_chat") },
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
        ) {
            // Search bar & Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clickable { navController.navigate("social_search") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Search", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                val avatarUrl by viewModel.avatarUrl.collectAsState()
                
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("social_settings") },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Profile & Settings",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile & Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Pill filters
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ChatFilter.entries.size) { index ->
                    val filter = ChatFilter.entries[index]
                    FilterPill(
                        text = filter.title,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            if (!isSessionVerified) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Verify this device",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To read your encrypted chat history, verify this login from another device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                showVerificationSheet = true
                                viewModel.startDeviceVerification()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Verify Device")
                        }
                    }
                }
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && rooms.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    ExpressivePullToRefreshBox(
                        isRefreshing = isLoading,
                        onRefresh = { viewModel.loadRooms() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RealChatList(
                            items = filteredRooms, 
                            navController = navController,
                            viewModel = viewModel,
                            selectedFilter = selectedFilter,
                            onLongPress = { selectedRoomForOptions = it }
                        )
                    }
                }
            }
        }
    }

    if (showVerificationSheet) {
        val verificationState by viewModel.verificationState.collectAsState()
        val verificationEmojis by viewModel.verificationEmojis.collectAsState()
        
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.cancelVerification()
                viewModel.resetVerification()
                showVerificationSheet = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Device Verification",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                when (verificationState) {
                    SocialViewModel.VerificationState.INITIAL -> {
                        Text(
                            text = "Start interactive verification to securely link this device with your existing Matrix account.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = { viewModel.startDeviceVerification() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Verification")
                        }
                    }
                    SocialViewModel.VerificationState.REQUESTED -> {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        Text(
                            text = "Waiting for your other device to accept the verification request...",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        OutlinedButton(
                            onClick = {
                                viewModel.cancelVerification()
                                viewModel.resetVerification()
                                showVerificationSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                    SocialViewModel.VerificationState.ACCEPTED -> {
                        Text(
                            text = "Request accepted! Click the button below to start the emoji comparison process.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = { viewModel.startSasVerification() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Emoji Comparison")
                        }
                    }
                    SocialViewModel.VerificationState.SAS_STARTED -> {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        Text(
                            text = "Starting secure emoji exchange...",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                    SocialViewModel.VerificationState.RECEIVED_EMOJIS -> {
                        Text(
                            text = "Compare the emojis below with the ones shown on your other device. Do they match?",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val rows = verificationEmojis.chunked(4)
                            rows.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { emoji ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = emoji.symbol(),
                                                fontSize = 36.sp,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = emoji.description(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    if (row.size < 4) {
                                        repeat(4 - row.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.declineVerification()
                                    viewModel.resetVerification()
                                    showVerificationSheet = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("They Don't Match")
                            }
                            Button(
                                onClick = { viewModel.approveVerification() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("They Match")
                            }
                        }
                    }
                    SocialViewModel.VerificationState.FINISHED -> {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Your device is now verified!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "You can now securely read and send encrypted messages.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.resetVerification()
                                showVerificationSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close")
                        }
                    }
                    SocialViewModel.VerificationState.CANCELLED, SocialViewModel.VerificationState.FAILED -> {
                        Text(
                            text = "Verification failed or was cancelled.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = { viewModel.startDeviceVerification() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }

    if (selectedRoomForOptions != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedRoomForOptions = null }
        ) {
            val room = selectedRoomForOptions!!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 16.dp)
            ) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()

                ListItem(
                    headlineContent = { Text(if (room.isDirect) "View User Profile" else "View Group Info") },
                    leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.clickable {
                        if (room.isDirect) {
                            scope.launch {
                                try {
                                    val client = viewModel.matrixManager.client
                                    val targetRoom = client?.rooms()?.find { it.id() == room.id }
                                    val currentUserId = client?.userId()
                                    var otherUserId: String? = null
                                    if (targetRoom != null && currentUserId != null) {
                                        val iterator = targetRoom.membersNoSync()
                                        if (iterator != null) {
                                            while (true) {
                                                val chunk = iterator.nextChunk(100u)
                                                if (chunk == null || chunk.isEmpty()) break
                                                val otherMember = chunk.find { it.userId() != currentUserId }
                                                if (otherMember != null) {
                                                    otherUserId = otherMember.userId()
                                                    break
                                                }
                                            }
                                        }
                                    }
                                    if (otherUserId != null) {
                                        viewingUserId = otherUserId
                                        viewingProfileName = room.name
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            selectedGroupRoomId = room.id
                            selectedGroupName = room.name
                            roomMembers = null
                            showGroupInfo = true
                        }
                        selectedRoomForOptions = null
                    }
                )

                ListItem(
                    headlineContent = { Text(if (room.isMuted) "Unmute" else "Mute") },
                    leadingContent = { 
                        Icon(
                            if (room.isMuted) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff, 
                            contentDescription = null
                        ) 
                    },
                    modifier = Modifier.clickable {
                        viewModel.roomListManager.toggleMute(room.id, room.isMuted)
                        selectedRoomForOptions = null
                    }
                )

                ListItem(
                    headlineContent = { Text(if (room.isPinned) "Unpin" else "Pin") },
                    leadingContent = { 
                        Icon(
                            if (room.isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin, 
                            contentDescription = null
                        ) 
                    },
                    modifier = Modifier.clickable {
                        viewModel.roomListManager.togglePin(room.id, room.isPinned)
                        selectedRoomForOptions = null
                    }
                )

                if (room.isDirect) {
                    ListItem(
                        headlineContent = { Text("Block User", color = MaterialTheme.colorScheme.error) },
                        leadingContent = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable {
                            viewModel.roomListManager.blockUser(room.id)
                            selectedRoomForOptions = null
                        }
                    )
                } else {
                    ListItem(
                        headlineContent = { Text("Leave Group", color = MaterialTheme.colorScheme.error) },
                        leadingContent = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable {
                            viewModel.leaveRoom(room.id)
                            selectedRoomForOptions = null
                        }
                    )
                }
            }
        }
    }

    if (viewingUserId != null) {
        ModalBottomSheet(onDismissRequest = { viewingUserId = null; viewingProfileName = null }) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewingProfileName ?: viewingUserId!!, style = MaterialTheme.typography.headlineMedium)
                Text(viewingUserId!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))
                
                val context = androidx.compose.ui.platform.LocalContext.current
                Button(onClick = {
                    val clipboard = androidx.core.content.ContextCompat.getSystemService(context, android.content.ClipboardManager::class.java)
                    clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("User ID", viewingUserId))
                    android.widget.Toast.makeText(context, "ID Copied", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy Matrix ID")
                }
                Spacer(modifier = Modifier.height(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }

    if (showGroupInfo && selectedGroupRoomId != null) {
        LaunchedEffect(selectedGroupRoomId) {
            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val client = viewModel.matrixManager.client
                    val targetRoom = client?.rooms()?.find { it.id() == selectedGroupRoomId }
                    val iterator = targetRoom?.membersNoSync()
                    if (iterator != null) {
                        val list = mutableListOf<org.matrix.rustcomponents.sdk.RoomMember>()
                        while (true) {
                            val chunk = iterator.nextChunk(100u)
                            if (chunk == null || chunk.isEmpty()) break
                            list.addAll(chunk)
                        }
                        roomMembers = list
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        ModalBottomSheet(onDismissRequest = { showGroupInfo = false; selectedGroupRoomId = null; selectedGroupName = null }) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(16.dp)) {
                Text(selectedGroupName ?: "Group Info", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
                Text("Members: ${roomMembers?.size ?: "Loading..."}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (roomMembers != null) {
                    LazyColumn {
                        items(roomMembers!!.size) { index ->
                            val member = roomMembers!![index]
                            ListItem(
                                headlineContent = { Text(member.displayName() ?: member.userId()) },
                                supportingContent = { Text(member.userId(), style = MaterialTheme.typography.bodySmall) },
                                leadingContent = { 
                                    Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(40.dp))
                                },
                                trailingContent = {
                                    val role = member.suggestedRoleForPowerLevel()
                                    if (role == uniffi.matrix_sdk.RoomMemberRole.ADMINISTRATOR) {
                                        Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(12.dp)) {
                                            Text("Admin", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                                        }
                                    } else if (role == uniffi.matrix_sdk.RoomMemberRole.MODERATOR) {
                                        Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(12.dp)) {
                                            Text("Mod", color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                },
                                modifier = Modifier.clickable {
                                    viewingUserId = member.userId()
                                    viewingProfileName = member.displayName() ?: member.userId()
                                    showGroupInfo = false
                                }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RealChatList(
    items: List<RoomInfo>, 
    navController: NavController,
    viewModel: SocialViewModel,
    selectedFilter: ChatFilter,
    onLongPress: (RoomInfo) -> Unit
) {
    val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 80.dp

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val message = when (selectedFilter) {
                    ChatFilter.ALL -> "No chats found."
                    ChatFilter.UNREAD -> "You're all caught up! No unread chats."
                    ChatFilter.PEOPLE -> "No direct messages yet."
                    ChatFilter.ROOMS -> "No rooms or groups yet."
                    ChatFilter.FAVOURITES -> "No favorite chats yet.\nLong-press a chat to pin it."
                    ChatFilter.INVITES -> "No pending invites."
                }
                Text(
                    text = message, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding)
    ) {
        items(items.size) { index ->
            val room = items[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { navController.navigate("chat/${room.id}") },
                        onLongClick = { onLongPress(room) }
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val roomAvatar = viewModel.convertMxcToHttp(room.avatarUrl)
                if (roomAvatar != null) {
                    AsyncImage(
                        model = roomAvatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (room.isPinned) {
                            Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (room.isMuted) {
                            Icon(Icons.Default.NotificationsOff, contentDescription = null, modifier = Modifier.size(14.dp).padding(end = 4.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = room.lastMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                
                if (room.unreadCount > 0) {
                    Badge {
                        Text(room.unreadCount.toString())
                    }
                }
            }
        }
    }
}
