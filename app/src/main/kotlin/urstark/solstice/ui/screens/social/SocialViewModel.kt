package urstark.solstice.ui.screens.social

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import urstark.solstice.matrix.MatrixClientManager
import urstark.solstice.matrix.MatrixRestApiClient
import urstark.solstice.matrix.Device
import urstark.solstice.matrix.MatrixRoomListManager
import urstark.solstice.matrix.PushRulesResponse
import urstark.solstice.matrix.ThreePid
import kotlinx.coroutines.launch
import org.json.JSONObject
import urstark.solstice.matrix.PublicRoomChunk
import urstark.solstice.matrix.UserSearchResult
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val matrixManager: MatrixClientManager,
    val roomListManager: MatrixRoomListManager,
    val matrixRestApiClient: MatrixRestApiClient
) : ViewModel() {
    private val prefs = context.getSharedPreferences("social_prefs", Context.MODE_PRIVATE)

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    private val _pushRules = MutableStateFlow<PushRulesResponse?>(null)
    val pushRules: StateFlow<PushRulesResponse?> = _pushRules.asStateFlow()

    private val _threePids = MutableStateFlow<List<ThreePid>>(emptyList())
    val threePids: StateFlow<List<ThreePid>> = _threePids.asStateFlow()
    
    private val _identityServer = MutableStateFlow<String?>("https://vector.im")
    val identityServer: StateFlow<String?> = _identityServer.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _currentDeviceId = MutableStateFlow<String?>(null)
    val currentDeviceId: StateFlow<String?> = _currentDeviceId.asStateFlow()

    private val _isSessionVerified = MutableStateFlow(false)
    val isSessionVerified: StateFlow<Boolean> = _isSessionVerified.asStateFlow()

    enum class VerificationState {
        INITIAL,
        REQUESTED,
        ACCEPTED,
        SAS_STARTED,
        RECEIVED_EMOJIS,
        FINISHED,
        CANCELLED,
        FAILED
    }

    private val _verificationState = MutableStateFlow(VerificationState.INITIAL)
    val verificationState: StateFlow<VerificationState> = _verificationState.asStateFlow()

    private val _verificationEmojis = MutableStateFlow<List<org.matrix.rustcomponents.sdk.SessionVerificationEmoji>>(emptyList())
    val verificationEmojis: StateFlow<List<org.matrix.rustcomponents.sdk.SessionVerificationEmoji>> = _verificationEmojis.asStateFlow()

    private var verificationController: org.matrix.rustcomponents.sdk.SessionVerificationController? = null

    private val _ignoredUsers = MutableStateFlow<List<String>>(emptyList())
    val ignoredUsers: StateFlow<List<String>> = _ignoredUsers.asStateFlow()

    private val _isLoadingSettings = MutableStateFlow(false)
    val isLoadingSettings: StateFlow<Boolean> = _isLoadingSettings.asStateFlow()

    private val _enableAllNotifications = MutableStateFlow(prefs.getBoolean("enable_all", true))
    val enableAllNotifications: StateFlow<Boolean> = _enableAllNotifications.asStateFlow()

    private val _muteGroups = MutableStateFlow(prefs.getBoolean("mute_groups", false))
    val muteGroups: StateFlow<Boolean> = _muteGroups.asStateFlow()

    private val _muteDMs = MutableStateFlow(prefs.getBoolean("mute_dms", false))
    val muteDMs: StateFlow<Boolean> = _muteDMs.asStateFlow()

    // Rich Notifications (Matrix Native)
    private val _dmNotificationMode = MutableStateFlow<org.matrix.rustcomponents.sdk.RoomNotificationMode?>(null)
    val dmNotificationMode: StateFlow<org.matrix.rustcomponents.sdk.RoomNotificationMode?> = _dmNotificationMode.asStateFlow()

    private val _groupNotificationMode = MutableStateFlow<org.matrix.rustcomponents.sdk.RoomNotificationMode?>(null)
    val groupNotificationMode: StateFlow<org.matrix.rustcomponents.sdk.RoomNotificationMode?> = _groupNotificationMode.asStateFlow()

    private val _isRoomMentionEnabled = MutableStateFlow(false)
    val isRoomMentionEnabled: StateFlow<Boolean> = _isRoomMentionEnabled.asStateFlow()

    private val _notificationSoundName = MutableStateFlow(prefs.getString("notification_sound_name", "Default") ?: "Default")
    val notificationSoundName: StateFlow<String> = _notificationSoundName.asStateFlow()

    // Screen Lock
    private val _isScreenLockEnabled = MutableStateFlow(prefs.getBoolean("screen_lock_enabled", false))
    val isScreenLockEnabled: StateFlow<Boolean> = _isScreenLockEnabled.asStateFlow()

    // Advanced Settings Toggles
    private val _sharePresence = MutableStateFlow(prefs.getBoolean("share_presence", true))
    val sharePresence: StateFlow<Boolean> = _sharePresence.asStateFlow()

    private val _optimizeMediaQuality = MutableStateFlow(prefs.getBoolean("optimize_media_quality", true))
    val optimizeMediaQuality: StateFlow<Boolean> = _optimizeMediaQuality.asStateFlow()

    private val _showMediaInTimeline = MutableStateFlow(prefs.getString("show_media_in_timeline", "Always") ?: "Always")
    val showMediaInTimeline: StateFlow<String> = _showMediaInTimeline.asStateFlow()

    init {
        // Observe login state and load rooms when logged in
    }
    
    fun loadRooms() {
        roomListManager.loadRooms()
    }

    fun setEnableAllNotifications(enabled: Boolean) {
        prefs.edit().putBoolean("enable_all", enabled).apply()
        _enableAllNotifications.value = enabled
    }

    fun setMuteGroups(muted: Boolean) {
        prefs.edit().putBoolean("mute_groups", muted).apply()
        _muteGroups.value = muted
    }

    fun setMuteDMs(muted: Boolean) {
        prefs.edit().putBoolean("mute_dms", muted).apply()
        _muteDMs.value = muted
    }

    fun setScreenLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("screen_lock_enabled", enabled).apply()
        _isScreenLockEnabled.value = enabled
    }

    fun setSharePresence(enabled: Boolean) {
        prefs.edit().putBoolean("share_presence", enabled).apply()
        _sharePresence.value = enabled
    }

    fun setOptimizeMediaQuality(enabled: Boolean) {
        prefs.edit().putBoolean("optimize_media_quality", enabled).apply()
        _optimizeMediaQuality.value = enabled
    }

    fun setShowMediaInTimeline(value: String) {
        prefs.edit().putString("show_media_in_timeline", value).apply()
        _showMediaInTimeline.value = value
    }

    suspend fun searchPublicRooms(searchTerm: String, limit: Int = 50): List<PublicRoomChunk> {
        val client = matrixManager.client ?: return emptyList()
        return matrixRestApiClient.searchPublicRooms(client, searchTerm, limit)
    }

    suspend fun searchUsers(searchTerm: String, limit: Int = 50): List<UserSearchResult> {
        val client = matrixManager.client ?: return emptyList()
        return matrixRestApiClient.searchUsers(client, searchTerm, limit)
    }

    suspend fun createSpace(name: String, isPublic: Boolean): String? {
        val client = matrixManager.client ?: return null
        return matrixRestApiClient.createSpace(client, name, isPublic)
    }

    private fun loadIgnoredUsers(client: org.matrix.rustcomponents.sdk.Client) {
        try {
            val ignoredData = client.accountData("m.ignored_user_list")
            if (!ignoredData.isNullOrBlank()) {
                val json = JSONObject(ignoredData)
                val ignoredUsersObj = json.optJSONObject("ignored_users")
                val list = mutableListOf<String>()
                if (ignoredUsersObj != null) {
                    val keys = ignoredUsersObj.keys()
                    while (keys.hasNext()) {
                        list.add(keys.next())
                    }
                }
                _ignoredUsers.value = list
            } else {
                _ignoredUsers.value = emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _ignoredUsers.value = emptyList()
        }
    }

    fun loadSettingsData() {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            _isLoadingSettings.value = true
            try {
                try {
                    _devices.value = matrixRestApiClient.getDevices(client)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    _pushRules.value = matrixRestApiClient.getPushRules(client)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    _threePids.value = matrixRestApiClient.get3PIDs(client)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    loadIgnoredUsers(client)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                try {
                    val notifSettings = client.getNotificationSettings()
                    _dmNotificationMode.value = notifSettings.getDefaultRoomNotificationMode(isEncrypted = true, isOneToOne = true)
                    _groupNotificationMode.value = notifSettings.getDefaultRoomNotificationMode(isEncrypted = true, isOneToOne = false)
                    _isRoomMentionEnabled.value = notifSettings.isRoomMentionEnabled()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    val accountData = client.accountData("m.identity_server")
                    if (!accountData.isNullOrBlank()) {
                        val json = JSONObject(accountData)
                        _identityServer.value = json.optString("base_url", null)
                    } else {
                        _identityServer.value = null
                    }
                } catch (e: Exception) {
                    _identityServer.value = null
                }

                try {
                    _currentDeviceId.value = client.deviceId()
                    _isSessionVerified.value = client.getSessionVerificationController().isVerified()
                    
                    // Fetch profile from server to ensure it is up to date
                    try {
                        val profile = client.getProfile(client.userId())
                        _avatarUrl.value = convertMxcToHttp(profile.avatarUrl) ?: convertMxcToHttp(client.avatarUrl())
                        _displayName.value = profile.displayName ?: client.displayName()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Fallback to cached local values
                        try {
                            _avatarUrl.value = convertMxcToHttp(client.avatarUrl())
                            _displayName.value = client.displayName()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingSettings.value = false
            }
        }
    }
    
    fun setIdentityServer(url: String?) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                if (url == null) {
                    client.setAccountData("m.identity_server", "{}")
                    _identityServer.value = null
                } else {
                    client.setAccountData("m.identity_server", "{\"base_url\": \"$url\"}")
                    _identityServer.value = url
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun togglePushRule(kind: String, ruleId: String, enabled: Boolean) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            val success = matrixRestApiClient.setPushRuleEnabled(client, kind, ruleId, enabled)
            if (success) {
                _pushRules.value = matrixRestApiClient.getPushRules(client)
            }
        }
    }

    fun convertMxcToHttp(mxc: String?): String? {
        if (mxc == null || !mxc.startsWith("mxc://")) return null
        val client = matrixManager.client ?: return null
        val hs = client.session().homeserverUrl.removeSuffix("/")
        val stripped = mxc.removePrefix("mxc://")
        val parts = stripped.split("/")
        if (parts.size != 2) return null
        return "$hs/_matrix/media/v3/download/${parts[0]}/${parts[1]}"
    }

    fun uploadAvatar(mimeType: String, bytes: ByteArray) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                client.uploadAvatar(mimeType, bytes)
                try {
                    val profile = client.getProfile(client.userId())
                    _avatarUrl.value = convertMxcToHttp(profile.avatarUrl)
                } catch (e: Exception) {
                    _avatarUrl.value = convertMxcToHttp(client.avatarUrl())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setDisplayName(name: String) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                client.setDisplayName(name)
                try {
                    val profile = client.getProfile(client.userId())
                    _displayName.value = profile.displayName
                } catch (e: Exception) {
                    _displayName.value = client.displayName()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun leaveRoom(roomId: String) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                val room = client.rooms().find { it.id() == roomId }
                room?.leave()
                roomListManager.loadRooms()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun joinRoom(roomIdOrAlias: String): Boolean {
        val client = matrixManager.client ?: return false
        return matrixRestApiClient.joinRoom(client, roomIdOrAlias)
    }

    suspend fun getPinnedEvents(roomId: String): List<String> {
        val client = matrixManager.client ?: return emptyList()
        return matrixRestApiClient.getPinnedEvents(client, roomId)
    }

    suspend fun pinEvent(roomId: String, eventId: String, pin: Boolean) {
        val client = matrixManager.client ?: return
        val currentPinned = matrixRestApiClient.getPinnedEvents(client, roomId)
        val newPinned = if (pin) {
            if (currentPinned.contains(eventId)) currentPinned else currentPinned + eventId
        } else {
            currentPinned.filter { it != eventId }
        }
        matrixRestApiClient.setPinnedEvents(client, roomId, newPinned)
    }

    fun unblockUser(userId: String) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                client.unignoreUser(userId)
                loadIgnoredUsers(client)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun signOutOtherSessions() {
        val client = matrixManager.client ?: return
        val currentId = client.deviceId()
        viewModelScope.launch {
            _isLoadingSettings.value = true
            try {
                val allDevices = matrixRestApiClient.getDevices(client)
                val otherDeviceIds = allDevices.map { it.device_id }.filter { it != currentId }
                if (otherDeviceIds.isNotEmpty()) {
                    matrixRestApiClient.deleteDevices(client, otherDeviceIds)
                }
                _devices.value = matrixRestApiClient.getDevices(client)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingSettings.value = false
            }
        }
    }

    fun signOutSession(deviceId: String) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            _isLoadingSettings.value = true
            try {
                matrixRestApiClient.deleteDevices(client, listOf(deviceId))
                _devices.value = matrixRestApiClient.getDevices(client)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingSettings.value = false
            }
        }
    }

    fun setDmNotificationMode(mode: org.matrix.rustcomponents.sdk.RoomNotificationMode) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                val notifSettings = client.getNotificationSettings()
                notifSettings.setDefaultRoomNotificationMode(isEncrypted = true, isOneToOne = true, mode = mode)
                notifSettings.setDefaultRoomNotificationMode(isEncrypted = false, isOneToOne = true, mode = mode)
                _dmNotificationMode.value = mode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setGroupNotificationMode(mode: org.matrix.rustcomponents.sdk.RoomNotificationMode) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                val notifSettings = client.getNotificationSettings()
                notifSettings.setDefaultRoomNotificationMode(isEncrypted = true, isOneToOne = false, mode = mode)
                notifSettings.setDefaultRoomNotificationMode(isEncrypted = false, isOneToOne = false, mode = mode)
                _groupNotificationMode.value = mode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setRoomMentionEnabled(enabled: Boolean) {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                val notifSettings = client.getNotificationSettings()
                notifSettings.setRoomMentionEnabled(enabled)
                _isRoomMentionEnabled.value = enabled
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setNotificationSound(uri: String?, name: String) {
        prefs.edit().putString("notification_sound_uri", uri).putString("notification_sound_name", name).apply()
        _notificationSoundName.value = name
    }

    fun startDeviceVerification() {
        val client = matrixManager.client ?: return
        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.REQUESTED
                val controller = client.getSessionVerificationController()
                verificationController = controller
                controller.setDelegate(object : org.matrix.rustcomponents.sdk.SessionVerificationControllerDelegate {
                    override fun didAcceptVerificationRequest() {
                        _verificationState.value = VerificationState.ACCEPTED
                    }

                    override fun didStartSasVerification() {
                        _verificationState.value = VerificationState.SAS_STARTED
                    }

                    override fun didReceiveVerificationData(data: org.matrix.rustcomponents.sdk.SessionVerificationData) {
                        if (data is org.matrix.rustcomponents.sdk.SessionVerificationData.Emojis) {
                            _verificationEmojis.value = data.emojis
                            _verificationState.value = VerificationState.RECEIVED_EMOJIS
                        }
                    }

                    override fun didFail() {
                        _verificationState.value = VerificationState.FAILED
                    }

                    override fun didCancel() {
                        _verificationState.value = VerificationState.CANCELLED
                    }

                    override fun didFinish() {
                        _verificationState.value = VerificationState.FINISHED
                        _isSessionVerified.value = true
                    }
                })
                controller.requestVerification()
            } catch (e: Exception) {
                e.printStackTrace()
                _verificationState.value = VerificationState.FAILED
            }
        }
    }

    fun startSasVerification() {
        viewModelScope.launch {
            try {
                verificationController?.startSasVerification()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun approveVerification() {
        viewModelScope.launch {
            try {
                verificationController?.approveVerification()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun declineVerification() {
        viewModelScope.launch {
            try {
                verificationController?.declineVerification()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelVerification() {
        viewModelScope.launch {
            try {
                verificationController?.cancelVerification()
                _verificationState.value = VerificationState.INITIAL
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetVerification() {
        _verificationState.value = VerificationState.INITIAL
        _verificationEmojis.value = emptyList()
        verificationController = null
    }
}
