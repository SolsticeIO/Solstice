package urstark.solstice.matrix

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.matrix.rustcomponents.sdk.Client
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class PublicRoomsRequest(
    val limit: Int = 50,
    val filter: RoomFilter? = null
)

@Serializable
data class RoomFilter(
    val generic_search_term: String? = null
)

@Serializable
data class PublicRoomsResponse(
    val chunk: List<PublicRoomChunk> = emptyList(),
    val total_room_count_estimate: Int = 0
)

@Serializable
data class PublicRoomChunk(
    val room_id: String,
    val name: String? = null,
    val topic: String? = null,
    val canonical_alias: String? = null,
    val num_joined_members: Int = 0,
    val avatar_url: String? = null
)

@Serializable
data class CreateRoomRequest(
    val name: String,
    val visibility: String,
    val preset: String? = null,
    val creation_content: Map<String, String>? = null
)

@Serializable
data class CreateRoomResponse(
    val room_id: String
)

@Serializable
data class UserSearchRequest(
    val search_term: String,
    val limit: Int = 50
)

@Serializable
data class UserSearchResponse(
    val results: List<UserSearchResult> = emptyList(),
    val limited: Boolean = false
)

@Serializable
data class UserSearchResult(
    val user_id: String,
    val display_name: String? = null,
    val avatar_url: String? = null
)

@Serializable
data class DevicesResponse(
    val devices: List<Device> = emptyList()
)

@Serializable
data class Device(
    val device_id: String,
    val display_name: String? = null,
    val last_seen_ip: String? = null,
    val last_seen_ts: Long? = null
)

@Serializable
data class ThreePidsResponse(
    val threepids: List<ThreePid> = emptyList()
)

@Serializable
data class ThreePid(
    val medium: String,
    val address: String,
    val validated_at: Long = 0,
    val added_at: Long = 0
)

@Serializable
data class PushRulesResponse(
    val global: GlobalPushRules = GlobalPushRules()
)

@Serializable
data class GlobalPushRules(
    val override: List<PushRule> = emptyList(),
    val underride: List<PushRule> = emptyList(),
    val sender: List<PushRule> = emptyList(),
    val room: List<PushRule> = emptyList(),
    val content: List<PushRule> = emptyList()
)

@Serializable
data class PushRule(
    val rule_id: String,
    val default: Boolean = false,
    val enabled: Boolean = true
)

@Serializable
data class PushRuleEnableRequest(
    val enabled: Boolean
)

@Singleton
class MatrixRestApiClient @Inject constructor() {
    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    suspend fun searchPublicRooms(client: Client, searchTerm: String, limit: Int = 50): List<PublicRoomChunk> {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            
            val response: PublicRoomsResponse = httpClient.post("$hsUrl/_matrix/client/v3/publicRooms") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(PublicRoomsRequest(limit = limit, filter = RoomFilter(generic_search_term = searchTerm)))
            }.body()
            response.chunk
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun searchUsers(client: Client, searchTerm: String, limit: Int = 50): List<UserSearchResult> {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            
            val response: UserSearchResponse = httpClient.post("$hsUrl/_matrix/client/v3/user_directory/search") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(UserSearchRequest(search_term = searchTerm, limit = limit))
            }.body()
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun createSpace(client: Client, name: String, isPublic: Boolean): String? {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            
            val request = CreateRoomRequest(
                name = name,
                visibility = if (isPublic) "public" else "private",
                preset = if (isPublic) "public_chat" else "private_chat",
                creation_content = mapOf("type" to "m.space")
            )
            val response: CreateRoomResponse = httpClient.post("$hsUrl/_matrix/client/v3/createRoom") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            response.room_id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDevices(client: Client): List<Device> {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            val response: DevicesResponse = httpClient.get("$hsUrl/_matrix/client/v3/devices") {
                header("Authorization", "Bearer $token")
            }.body()
            response.devices
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteDevices(client: Client, deviceIds: List<String>): Boolean {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            val response = httpClient.post("$hsUrl/_matrix/client/v3/delete_devices") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(mapOf("devices" to deviceIds))
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun get3PIDs(client: Client): List<ThreePid> {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            val response: ThreePidsResponse = httpClient.get("$hsUrl/_matrix/client/v3/account/3pid") {
                header("Authorization", "Bearer $token")
            }.body()
            response.threepids
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPushRules(client: Client): PushRulesResponse? {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            httpClient.get("$hsUrl/_matrix/client/v3/pushrules") {
                header("Authorization", "Bearer $token")
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun setPushRuleEnabled(client: Client, kind: String, ruleId: String, enabled: Boolean): Boolean {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            
            val response = httpClient.put("$hsUrl/_matrix/client/v3/pushrules/global/$kind/$ruleId/enabled") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(PushRuleEnableRequest(enabled))
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun joinRoom(client: Client, roomIdOrAlias: String): Boolean {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            
            val response = httpClient.post("$hsUrl/_matrix/client/v3/join/$roomIdOrAlias") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody("{}")
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPinnedEvents(client: Client, roomId: String): List<String> {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            val response: PinnedEventsContent = httpClient.get("$hsUrl/_matrix/client/v3/rooms/$roomId/state/m.room.pinned_events/") {
                header("Authorization", "Bearer $token")
            }.body()
            response.pinned
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setPinnedEvents(client: Client, roomId: String, pinnedIds: List<String>): Boolean {
        return try {
            val session = client.session()
            val hsUrl = session.homeserverUrl.removeSuffix("/")
            val token = session.accessToken
            val response = httpClient.put("$hsUrl/_matrix/client/v3/rooms/$roomId/state/m.room.pinned_events/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(PinnedEventsContent(pinnedIds))
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Serializable
data class PinnedEventsContent(
    val pinned: List<String> = emptyList()
)
