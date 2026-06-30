package urstark.solstice.matrix

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.matrix.rustcomponents.sdk.*
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatrixClientManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var client: Client? = null
        private set
        
    private var syncService: SyncService? = null
    
    private var taskHandle: TaskHandle? = null

    init {
        // Automatically restore session if it exists on disk
        scope.launch {
            try {
                val prefs = context.getSharedPreferences("matrix_session_prefs", Context.MODE_PRIVATE)
                val accessToken = prefs.getString("access_token", null)
                val userId = prefs.getString("user_id", null)
                val deviceId = prefs.getString("device_id", null)
                val homeserverUrl = prefs.getString("homeserver_url", null)
                
                if (accessToken != null && userId != null && deviceId != null && homeserverUrl != null) {
                    val refreshToken = prefs.getString("refresh_token", null)
                    val oidcData = prefs.getString("oidc_data", null)
                    val slidingSyncProxy = prefs.getString("sliding_sync_proxy", null)
                    
                    val session = Session(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        userId = userId,
                        deviceId = deviceId,
                        homeserverUrl = homeserverUrl,
                        oidcData = oidcData,
                        slidingSyncProxy = slidingSyncProxy
                    )
                    
                    val matrixDir = File(context.filesDir, "matrix_store").absolutePath
                    val baseClient = ClientBuilder()
                        .basePath(matrixDir)
                        .homeserverUrl(homeserverUrl)
                        .build()
                        
                    baseClient.restoreSession(session)
                    client = baseClient
                    _isLoggedIn.value = true
                    startSync()
                }
            } catch (e: Exception) {
                // Ignore initialization failures on cold start if not logged in
                e.printStackTrace()
            }
        }
    }

    suspend fun login(homeserver: String, username: String, password: String) {
        _isLoading.value = true
        _error.value = null
        try {
            val matrixDir = File(context.filesDir, "matrix_store").absolutePath
            val newClient = ClientBuilder()
                .homeserverUrl(if (homeserver.startsWith("http")) homeserver else "https://$homeserver")
                .basePath(matrixDir)
                .build()
                
            newClient.login(username, password, "Solstice", "solstice")
            
            // Save session details to SharedPreferences
            val session = newClient.session()
            val prefs = context.getSharedPreferences("matrix_session_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("access_token", session.accessToken)
                putString("refresh_token", session.refreshToken)
                putString("user_id", session.userId)
                putString("device_id", session.deviceId)
                putString("homeserver_url", session.homeserverUrl)
                putString("oidc_data", session.oidcData)
                putString("sliding_sync_proxy", session.slidingSyncProxy)
                apply()
            }

            client = newClient
            _isLoggedIn.value = true
            startSync()
        } catch (e: Exception) {
            _error.value = "Login failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun register(homeserver: String, username: String, password: String, email: String?) {
        _isLoading.value = true
        _error.value = null
        try {
            _error.value = "Registration directly via SDK is limited. Please use Element or Matrix.org to create an account first."
        } catch (e: Exception) {
            _error.value = "Registration failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun requestPasswordReset(homeserver: String, username: String) {
        _isLoading.value = true
        _error.value = null
        try {
            _error.value = "Password reset via Rust SDK pending implementation."
        } catch (e: Exception) {
            _error.value = "Reset failed: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() {
        scope.launch {
            try {
                val prefs = context.getSharedPreferences("matrix_session_prefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                
                syncService?.stop()
                client?.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoggedIn.value = false
                client = null
                syncService = null
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    private suspend fun startSync() {
        val safeClient = client ?: return
        try {
            syncService = safeClient.syncService().withCrossProcessLock("urstark.solstice.sync").finish()
            syncService?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
