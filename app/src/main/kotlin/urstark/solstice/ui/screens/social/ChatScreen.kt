/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 *
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. Per GPL-3.0 Section 4 & Section 5
 */
package urstark.solstice.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.matrix.rustcomponents.sdk.Room
import org.matrix.rustcomponents.sdk.TimelineChange
import org.matrix.rustcomponents.sdk.TimelineDiff
import org.matrix.rustcomponents.sdk.TimelineItem
import org.matrix.rustcomponents.sdk.TimelineListener
import org.matrix.rustcomponents.sdk.TypingNotificationsListener
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomId: String,
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    val messages = remember { mutableStateListOf<TimelineItem>() }
    var isTyping by remember { mutableStateOf(false) }
    var room by remember { mutableStateOf<Room?>(null) }
    var timeline by remember { mutableStateOf<org.matrix.rustcomponents.sdk.Timeline?>(null) }
    var typingJob by remember { mutableStateOf<Job?>(null) }
    var selectedItemForReaction by remember { mutableStateOf<org.matrix.rustcomponents.sdk.EventTimelineItem?>(null) }
    var replyingToItem by remember { mutableStateOf<org.matrix.rustcomponents.sdk.EventTimelineItem?>(null) }
    var editingItem by remember { mutableStateOf<org.matrix.rustcomponents.sdk.EventTimelineItem?>(null) }
    var selectionMode by remember { mutableStateOf(false) }
    var showGroupInfo by remember { mutableStateOf(false) }
    var viewingProfile by remember { mutableStateOf<org.matrix.rustcomponents.sdk.ProfileDetails.Ready?>(null) }
    var viewingUserId by remember { mutableStateOf<String?>(null) }
    var fetchedProfile by remember { mutableStateOf<org.matrix.rustcomponents.sdk.UserProfile?>(null) }
    
    LaunchedEffect(viewingUserId) {
        val userId = viewingUserId
        if (userId != null) {
            val client = viewModel.matrixManager.client
            if (client != null) {
                try {
                    fetchedProfile = client.getProfile(userId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            fetchedProfile = null
        }
    }
    val selectedItems = remember { mutableStateListOf<org.matrix.rustcomponents.sdk.EventTimelineItem>() }
    
    val roomMembersMap = remember { mutableStateMapOf<String, org.matrix.rustcomponents.sdk.RoomMember>() }
    val pinnedEventIds = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()

    fun loadPinnedEvents() {
        scope.launch {
            try {
                val pinned = viewModel.getPinnedEvents(roomId)
                pinnedEventIds.clear()
                pinnedEventIds.addAll(pinned)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(roomId) {
        var timelineListenerResult: Any? = null
        val client = viewModel.matrixManager.client
        if (client != null) {
            scope.launch {
                try {
                    val r = client.rooms().find { it.id() == roomId }
                    room = r
                    if (r != null) {
                        room?.subscribeToTypingNotifications(object : org.matrix.rustcomponents.sdk.TypingNotificationsListener {
                            override fun call(typingUserIds: List<String>) {
                                isTyping = typingUserIds.isNotEmpty() && !typingUserIds.contains(client.userId())
                            }
                        })
                        
                        val t = r.timeline()
                        timeline = t
                        
                        val listenerResult = t.addListener(object : TimelineListener {
                            override fun onUpdate(diffs: List<TimelineDiff>) {
                                for (diff in diffs) {
                                    when (diff.change()) {
                                        TimelineChange.APPEND -> messages.addAll(diff.append() ?: emptyList())
                                        TimelineChange.CLEAR -> messages.clear()
                                        TimelineChange.INSERT -> {
                                            val insert = diff.insert()
                                            if (insert != null) messages.add(insert.index.toInt(), insert.item)
                                        }
                                        TimelineChange.SET -> {
                                            val set = diff.set()
                                            if (set != null) messages[set.index.toInt()] = set.item
                                        }
                                        TimelineChange.REMOVE -> {
                                            val removeIdx = diff.remove()?.toInt()
                                            if (removeIdx != null) messages.removeAt(removeIdx)
                                        }
                                        TimelineChange.PUSH_BACK -> diff.pushBack()?.let { messages.add(it) }
                                        TimelineChange.PUSH_FRONT -> diff.pushFront()?.let { messages.add(0, it) }
                                        TimelineChange.POP_BACK -> if (messages.isNotEmpty()) messages.removeLast()
                                        TimelineChange.POP_FRONT -> if (messages.isNotEmpty()) messages.removeFirst()
                                        TimelineChange.TRUNCATE -> { }
                                        TimelineChange.RESET -> {
                                            messages.clear()
                                            messages.addAll(diff.reset() ?: emptyList())
                                        }
                                    }
                                }
                            }
                        })
                        timelineListenerResult = listenerResult
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        onDispose {
            try {
                val methods = timelineListenerResult?.javaClass?.methods ?: emptyArray()
                val getStreamMethod = methods.find { 
                    it.name == "getItemsStream" || it.name == "getTaskHandle" || 
                    it.name == "itemsStream" || it.name == "taskHandle" 
                }
                val handle = getStreamMethod?.invoke(timelineListenerResult) as? org.matrix.rustcomponents.sdk.TaskHandle
                handle?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            scope.launch {
                try {
                    room?.typingNotice(false)
                } catch (e: Exception) { }
            }
        }
    }

    LaunchedEffect(room) {
        val r = room ?: return@LaunchedEffect
        loadPinnedEvents()
        scope.launch(Dispatchers.IO) {
            try {
                val iterator = r.membersNoSync()
                if (iterator != null) {
                    while (true) {
                        val chunk = iterator.nextChunk(100u)
                        if (chunk == null || chunk.isEmpty()) break
                        for (member in chunk) {
                            roomMembersMap[member.userId()] = member
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("${selectedItems.size} Selected") },
                    navigationIcon = {
                        IconButton(onClick = { 
                            selectionMode = false
                            selectedItems.clear()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel Selection")
                        }
                    },
                    actions = {
                        if (selectedItems.isNotEmpty()) {
                            IconButton(onClick = {
                                scope.launch {
                                    for (item in selectedItems) {
                                        try {
                                            if (item.isOwn()) {
                                                room?.redact(item.eventId() ?: return@launch, "Bulk deleted")
                                            }
                                        } catch (e: Exception) { e.printStackTrace() }
                                    }
                                    selectionMode = false
                                    selectedItems.clear()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                val currentUserId = viewModel.matrixManager.client?.userId()
                val isDirect = room?.isDirect() == true
                val otherMember = if (isDirect) roomMembersMap.values.find { it.userId() != currentUserId } else null
                val title = if (isDirect) {
                    otherMember?.displayName() ?: room?.displayName() ?: roomId
                } else {
                    room?.displayName() ?: roomId
                }
                val subtitle = if (isDirect) {
                    otherMember?.userId() ?: "Direct Message"
                } else {
                    "Tap for info"
                }

                TopAppBar(
                    title = { 
                        Column(modifier = Modifier.clickable { 
                            if (isDirect && otherMember != null) {
                                viewingUserId = otherMember.userId()
                            } else {
                                showGroupInfo = true
                            }
                        }) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                val navBarPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = navBarPadding + 8.dp, top = 8.dp, start = 16.dp, end = 16.dp)
                ) {
                    if (replyingToItem != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().clickable { replyingToItem = null }
                        ) {
                            val replyMsg = replyingToItem?.content()?.asMessage()
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Replying to: ${replyMsg?.body() ?: "Message"}", style = MaterialTheme.typography.labelMedium)
                                IconButton(onClick = { replyingToItem = null }, modifier = Modifier.size(16.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel Reply")
                                }
                            }
                        }
                    }

                    if (editingItem != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().clickable { editingItem = null; messageText = "" }
                        ) {
                            val editMsg = editingItem?.content()?.asMessage()
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Editing: ${editMsg?.body() ?: "Message"}", style = MaterialTheme.typography.labelMedium)
                                IconButton(onClick = { editingItem = null; messageText = "" }, modifier = Modifier.size(16.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel Edit")
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var showAttachmentSheet by remember { mutableStateOf(false) }
                        IconButton(onClick = { showAttachmentSheet = true }) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = MaterialTheme.colorScheme.primary)
                        }
                        
                        if (showAttachmentSheet) {
                            val context = LocalContext.current
                            ModalBottomSheet(onDismissRequest = { showAttachmentSheet = false }) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                    Text("Send Media", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                                    ListItem(
                                        headlineContent = { Text("Photo or Video") },
                                        modifier = Modifier.clickable {
                                            android.widget.Toast.makeText(context, "Attachment picker coming soon", android.widget.Toast.LENGTH_SHORT).show()
                                            showAttachmentSheet = false
                                        }
                                    )
                                    ListItem(
                                        headlineContent = { Text("Document") },
                                        modifier = Modifier.clickable {
                                            android.widget.Toast.makeText(context, "Attachment picker coming soon", android.widget.Toast.LENGTH_SHORT).show()
                                            showAttachmentSheet = false
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()))
                                }
                            }
                        }

                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { 
                                messageText = it 
                                typingJob?.cancel()
                                typingJob = scope.launch {
                                    try {
                                        room?.typingNotice(true)
                                        delay(3000)
                                        room?.typingNotice(false)
                                    } catch (e: Exception) { }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(if (editingItem != null) "Edit message..." else "Message...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    val textToSend = messageText
                                    val replyItem = replyingToItem
                                    val editItem = editingItem
                                    messageText = ""
                                    replyingToItem = null
                                    editingItem = null
                                    typingJob?.cancel()
                                    scope.launch {
                                        try {
                                            room?.typingNotice(false)
                                            val content = org.matrix.rustcomponents.sdk.messageEventContentFromMarkdown(textToSend)
                                            if (editItem != null) {
                                                timeline?.edit(content, editItem)
                                            } else if (replyItem != null) {
                                                timeline?.sendReply(content, replyItem)
                                            } else {
                                                timeline?.send(content)
                                            }
                                        } catch (e: Exception) { e.printStackTrace() }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(25.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        var roomMembers by remember { mutableStateOf<List<org.matrix.rustcomponents.sdk.RoomMember>?>(null) }
        LaunchedEffect(showGroupInfo) {
            if (showGroupInfo && roomMembers == null) {
                try {
                    val iterator = room?.membersNoSync()
                    if (iterator != null) {
                        val list = mutableListOf<org.matrix.rustcomponents.sdk.RoomMember>()
                        while (true) {
                            val chunk = iterator.nextChunk(100u)
                            if (chunk == null || chunk.isEmpty()) break
                            list.addAll(chunk)
                        }
                        roomMembers = list
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            state = listState
        ) {
            if (isTyping) {
                item {
                    val typingText = if (room?.isDirect() == true) "is typing..." else "Someone is typing..."
                    Text(
                        text = typingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
                    )
                }
            }
            items(messages.size) { index ->
                val item = messages[messages.size - 1 - index]
                val event = item.asEvent()
                if (event != null) {
                    val message = event.content().asMessage()
                    if (message != null) {
                        val member = roomMembersMap[event.sender()]
                        val role = member?.suggestedRoleForPowerLevel()
                        val isRoomDirect = room?.isDirect() == true
                        ChatBubble(
                            event = event, 
                            messageText = message.body(),
                            isSelected = selectedItems.contains(event),
                            senderRole = role,
                            isRoomDirect = isRoomDirect,
                            onClick = {
                                if (selectionMode) {
                                    if (selectedItems.contains(event)) selectedItems.remove(event)
                                    else selectedItems.add(event)
                                    if (selectedItems.isEmpty()) selectionMode = false
                                }
                            },
                            onLongPress = { 
                                if (!selectionMode) {
                                    selectedItemForReaction = event 
                                }
                            },
                            onSwipeToReply = { replyingToItem = event },
                            onAvatarClick = { 
                                val profile = event.senderProfile() as? org.matrix.rustcomponents.sdk.ProfileDetails.Ready
                                viewingProfile = profile
                                viewingUserId = event.sender()
                            },
                            onReplyClick = { repliedEventId ->
                                val idx = messages.indexOfFirst { it.asEvent()?.eventId() == repliedEventId }
                                if (idx != -1) {
                                    scope.launch {
                                        val lazyColumnIndex = messages.size - 1 - idx
                                        listState.animateScrollToItem(lazyColumnIndex)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        
        if (viewingUserId != null) {
            val resolvedAvatarUrl = viewModel.convertMxcToHttp(fetchedProfile?.avatarUrl ?: viewingProfile?.avatarUrl)
            val resolvedDisplayName = fetchedProfile?.displayName ?: viewingProfile?.displayName ?: viewingUserId!!
            
            ModalBottomSheet(onDismissRequest = { viewingUserId = null; viewingProfile = null }) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (resolvedAvatarUrl != null) {
                        AsyncImage(
                            model = resolvedAvatarUrl,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(resolvedDisplayName, style = MaterialTheme.typography.headlineMedium)
                    Text(viewingUserId!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    val context = LocalContext.current
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
        
        if (showGroupInfo) {
            ModalBottomSheet(onDismissRequest = { showGroupInfo = false }) {
                Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(16.dp)) {
                    Text("Group Info", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
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
                                        val memberAvatar = viewModel.convertMxcToHttp(member.avatarUrl())
                                        if (memberAvatar != null) {
                                            AsyncImage(
                                                model = memberAvatar,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(40.dp))
                                        }
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
                                        showGroupInfo = false
                                    }
                                )
                            }
                        }
                    } else {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
        
        if (selectedItemForReaction != null) {
            ModalBottomSheet(onDismissRequest = { selectedItemForReaction = null }) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Message Actions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val emojis = listOf("👍", "❤️", "😂", "😮", "😢", "🔥")
                        emojis.forEach { emoji ->
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val t = room?.timeline()
                                            selectedItemForReaction!!.eventId()?.let { eventId ->
                                                t?.toggleReaction(eventId, emoji)
                                            }
                                        } catch (e: Exception) {}
                                        selectedItemForReaction = null
                                    }
                                }
                            ) {
                                Text(emoji, style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    ListItem(
                        headlineContent = { Text("Select") },
                        leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                        modifier = Modifier.clickable {
                            selectionMode = true
                            selectedItems.add(selectedItemForReaction!!)
                            selectedItemForReaction = null
                        }
                    )
                    
                    ListItem(
                        headlineContent = { Text("Reply") },
                        leadingContent = { Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null) },
                        modifier = Modifier.clickable {
                            replyingToItem = selectedItemForReaction
                            selectedItemForReaction = null
                        }
                    )
                    
                    val isPinned = selectedItemForReaction?.eventId()?.let { pinnedEventIds.contains(it) } == true
                    ListItem(
                        headlineContent = { Text(if (isPinned) "Unpin" else "Pin") },
                        leadingContent = { Icon(if (isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val eventId = selectedItemForReaction?.eventId()
                            if (eventId != null) {
                                scope.launch {
                                    try {
                                        viewModel.pinEvent(roomId, eventId, !isPinned)
                                        delay(300)
                                        loadPinnedEvents()
                                    } catch (e: Exception) { e.printStackTrace() }
                                }
                            }
                            selectedItemForReaction = null
                        }
                    )

                    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
                    ListItem(
                        headlineContent = { Text("Copy") },
                        leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val text = selectedItemForReaction?.content()?.asMessage()?.body()
                            if (text != null) {
                                clipboard.setText(androidx.compose.ui.text.buildAnnotatedString { append(text) })
                            }
                            selectedItemForReaction = null
                        }
                    )

                    if (selectedItemForReaction?.isOwn() == true) {
                        ListItem(
                            headlineContent = { Text("Edit") },
                            leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                            modifier = Modifier.clickable {
                                editingItem = selectedItemForReaction
                                messageText = selectedItemForReaction?.content()?.asMessage()?.body() ?: ""
                                selectedItemForReaction = null
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    try {
                                        room?.redact(selectedItemForReaction!!.eventId() ?: return@launch, "User deleted")
                                    } catch (e: Exception) { e.printStackTrace() }
                                }
                                selectedItemForReaction = null
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    event: org.matrix.rustcomponents.sdk.EventTimelineItem, 
    messageText: String, 
    isSelected: Boolean = false,
    senderRole: uniffi.matrix_sdk.RoomMemberRole? = null,
    isRoomDirect: Boolean = false,
    onClick: () -> Unit = {},
    onLongPress: () -> Unit,
    onSwipeToReply: () -> Unit,
    onAvatarClick: () -> Unit = {},
    onReplyClick: (String) -> Unit = {}
) {
    val isMine = event.isOwn()
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val baseBgColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else baseBgColor
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (isMine) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }
    
    val reactions = event.reactions()
    
    var offsetX by remember { mutableStateOf(0f) }
    var triggered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            modifier = Modifier
                .offset(x = offsetX.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = 0f
                            triggered = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount).coerceIn(-100f, 100f)
                            if (Math.abs(offsetX) > 80f && !triggered) {
                                onSwipeToReply()
                                triggered = true
                            }
                        }
                    )
                }
        ) {
            if (!isMine) {
                val senderProfile = event.senderProfile() as? org.matrix.rustcomponents.sdk.ProfileDetails.Ready
                val senderName = senderProfile?.displayName ?: event.sender()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = senderName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 4.dp, bottom = 2.dp)
                            .clickable { onAvatarClick() }
                    )
                    if (!isRoomDirect && senderRole != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        val badgeBg = if (senderRole == uniffi.matrix_sdk.RoomMemberRole.ADMINISTRATOR) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.tertiaryContainer
                        }
                        val badgeText = if (senderRole == uniffi.matrix_sdk.RoomMemberRole.ADMINISTRATOR) {
                            "Admin"
                        } else {
                            "Mod"
                        }
                        val badgeTextColor = if (senderRole == uniffi.matrix_sdk.RoomMemberRole.ADMINISTRATOR) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        }
                        if (senderRole == uniffi.matrix_sdk.RoomMemberRole.ADMINISTRATOR || senderRole == uniffi.matrix_sdk.RoomMemberRole.MODERATOR) {
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = badgeTextColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
            
            Surface(
                color = bgColor,
                shape = shape,
                modifier = Modifier.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongPress
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val inReplyTo = event.content().asMessage()?.inReplyTo()
                    val repliedToEvent = inReplyTo?.event as? org.matrix.rustcomponents.sdk.RepliedToEventDetails.Ready
                    if (repliedToEvent != null) {
                        val replyText = repliedToEvent.content.asMessage()?.body() ?: "Attachment"
                        val replySenderName = (repliedToEvent.senderProfile as? org.matrix.rustcomponents.sdk.ProfileDetails.Ready)?.displayName ?: repliedToEvent.sender
                        val targetEventId = inReplyTo.eventId
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(bottom = 8.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onReplyClick(targetEventId) }
                                .padding(start = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(replySenderName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(replyText, style = MaterialTheme.typography.bodySmall, maxLines = 1, color = textColor.copy(alpha = 0.8f))
                            }
                        }
                    }
                
                    val annotatedString = buildAnnotatedString {
                        val text = messageText
                        val matcher = android.util.Patterns.WEB_URL.matcher(text)
                        var lastIndex = 0
                        while (matcher.find()) {
                            val start = matcher.start()
                            val end = matcher.end()
                            append(text.substring(lastIndex, start))
                            pushStringAnnotation(tag = "URL", annotation = text.substring(start, end))
                            withStyle(style = SpanStyle(color = if (isMine) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                                append(text.substring(start, end))
                            }
                            pop()
                            lastIndex = end
                        }
                        append(text.substring(lastIndex))
                    }
                    
                    var showUrlDialog by remember { mutableStateOf<String?>(null) }
                    
                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                        onClick = { offset ->
                            val url = annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.item
                            if (url != null) {
                                showUrlDialog = url
                            } else {
                                onClick()
                            }
                        }
                    )
                    
                    if (showUrlDialog != null) {
                        val context = LocalContext.current
                        AlertDialog(
                            onDismissRequest = { showUrlDialog = null },
                            title = { Text("Open Link?") },
                            text = { Text("Are you sure you want to visit this external link: $showUrlDialog?") },
                            confirmButton = {
                                Button(onClick = { 
                                    var uriString = showUrlDialog!!
                                    if (!uriString.startsWith("http://") && !uriString.startsWith("https://")) {
                                        uriString = "https://$uriString"
                                    }
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uriString))
                                    context.startActivity(intent)
                                    showUrlDialog = null 
                                }) { Text("Yes, open") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showUrlDialog = null }) { Text("Cancel") }
                            }
                        )
                    }

                    // Ticks and Edited Indicators
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        if (event.content().asMessage()?.isEdited() == true) {
                            Text(
                                text = "edited",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.5f),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                        
                        if (isMine) {
                            val currentUserId = event.sender()
                            val receiptsMap = event.readReceipts()
                            val readByOthers = receiptsMap.keys.any { it != currentUserId }
                            
                            Icon(
                                imageVector = if (readByOthers) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = if (readByOthers) "Read" else "Sent",
                                tint = if (readByOthers) MaterialTheme.colorScheme.primary else textColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            
            if (reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    reactions.forEach { reaction ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${reaction.key} ${reaction.count}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
