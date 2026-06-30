package urstark.solstice.matrix

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.matrix.rustcomponents.sdk.RoomListEntry
import org.matrix.rustcomponents.sdk.RoomListLoadingState
import org.matrix.rustcomponents.sdk.RoomListService
import org.matrix.rustcomponents.sdk.RoomListServiceState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatrixRoomListManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clientManager: MatrixClientManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("matrix_room_prefs", Context.MODE_PRIVATE)

    // Data class to represent a room in the UI
    data class RoomInfo(
        val id: String,
        val name: String,
        val avatarUrl: String?,
        val unreadCount: Long,
        val highlightCount: Long,
        val lastMessage: String?,
        val isPinned: Boolean = false,
        val isMuted: Boolean = false,
        val isDirect: Boolean = false,
        val isInvite: Boolean = false,
        val isMarkedUnread: Boolean = false
    )

    private val _rooms = MutableStateFlow<List<RoomInfo>>(emptyList())
    val rooms: StateFlow<List<RoomInfo>> = _rooms.asStateFlow()

    private val _dms = MutableStateFlow<List<RoomInfo>>(emptyList())
    val dms: StateFlow<List<RoomInfo>> = _dms.asStateFlow()

    private val _groups = MutableStateFlow<List<RoomInfo>>(emptyList())
    val groups: StateFlow<List<RoomInfo>> = _groups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        scope.launch {
            clientManager.isLoggedIn.collectLatest { loggedIn ->
                if (loggedIn) {
                    // Start polling for room updates while logged in
                    while (true) {
                        loadRooms()
                        delay(3000) // Poll every 3 seconds
                    }
                } else {
                    _dms.value = emptyList()
                    _groups.value = emptyList()
                }
            }
        }
    }

    fun togglePin(roomId: String, currentlyPinned: Boolean) {
        val client = clientManager.client ?: return
        scope.launch {
            try {
                val room = client.rooms().find { it.id() == roomId } ?: return@launch
                // Use Double(0.5) for default priority, null to remove
                val priority = if (!currentlyPinned) 0.5 else null
                room.setIsFavourite(!currentlyPinned, priority)
                loadRooms() // Refresh list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleMute(roomId: String, currentlyMuted: Boolean) {
        val client = clientManager.client ?: return
        scope.launch {
            try {
                if (currentlyMuted) {
                    client.getNotificationSettings().unmuteRoom(roomId, true, false)
                } else {
                    client.getNotificationSettings().setRoomNotificationMode(roomId, org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE)
                }
                loadRooms() // Refresh list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun blockUser(roomId: String) {
        val client = clientManager.client ?: return
        scope.launch {
            try {
                val room = client.rooms().find { it.id() == roomId } ?: return@launch
                if (room.isDirect()) {
                    val iterator = room.membersNoSync()
                    if (iterator != null) {
                        var targetUserId: String? = null
                        val currentUserId = client.userId()
                        while (true) {
                            val chunk = iterator.nextChunk(100u)
                            if (chunk == null || chunk.isEmpty()) break
                            val otherMember = chunk.find { it.userId() != currentUserId }
                            if (otherMember != null) {
                                targetUserId = otherMember.userId()
                                break
                            }
                        }
                        if (targetUserId != null) {
                            client.ignoreUser(targetUserId)
                            loadRooms()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadRooms() {
        val client = clientManager.client ?: return
        // Only show loading indicator if lists are completely empty to prevent UI jank during polling
        if (_dms.value.isEmpty() && _groups.value.isEmpty()) {
            _isLoading.value = true
        }

        scope.launch {
            try {
                // Fetch all rooms from the client directly
                val allRooms = client.rooms()
                
                val dmsList = mutableListOf<RoomInfo>()
                val groupsList = mutableListOf<RoomInfo>()
                
                for (room in allRooms) {
                    val roomId = room.id()
                    
                    // Suspend call to get accurate server-side info (unread counts, favorites, etc.)
                    val roomInfo = room.roomInfo()
                    
                    val isPinned = roomInfo.isFavourite
                    val isMuted = roomInfo.userDefinedNotificationMode == org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE
                    val isDirect = room.isDirect()
                    
                    var lastMessageText = "Tap to view messages"
                    val lastEventItem = roomInfo.latestEvent
                    if (lastEventItem != null) {
                        val content = lastEventItem.content().asMessage()
                        val isMine = lastEventItem.isOwn()
                        val senderProfile = lastEventItem.senderProfile() as? org.matrix.rustcomponents.sdk.ProfileDetails.Ready
                        val sender = senderProfile?.displayName ?: lastEventItem.sender()
                        val body = content?.body() ?: "Message"
                        
                        lastMessageText = if (isMine) "You: $body" else if (isDirect) body else "$sender: $body"
                    }
                    
                    val info = RoomInfo(
                        id = roomId,
                        name = room.displayName() ?: "Unknown Room",
                        avatarUrl = room.avatarUrl(),
                        unreadCount = roomInfo.numUnreadMessages.toLong().coerceAtLeast(roomInfo.notificationCount.toLong()),
                        highlightCount = roomInfo.highlightCount.toLong(),
                        lastMessage = lastMessageText,
                        isPinned = isPinned,
                        isMuted = isMuted,
                        isDirect = room.isDirect() || roomInfo.isDirect,
                        isInvite = roomInfo.membership == org.matrix.rustcomponents.sdk.Membership.INVITED,
                        isMarkedUnread = roomInfo.isMarkedUnread
                    )
                    
                    if (info.isDirect) {
                        dmsList.add(info)
                    } else {
                        groupsList.add(info)
                    }
                }
                
                // Sort by pinned first, then by name
                val comparator = compareByDescending<RoomInfo> { it.isPinned }.thenBy { it.name }
                
                val allRoomsList = dmsList + groupsList
                _rooms.value = allRoomsList.sortedWith(comparator)
                _dms.value = dmsList.sortedWith(comparator)
                _groups.value = groupsList.sortedWith(comparator)
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
