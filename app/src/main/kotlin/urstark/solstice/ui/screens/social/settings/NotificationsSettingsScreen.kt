package urstark.solstice.ui.screens.social.settings

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import urstark.solstice.ui.screens.social.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val enableAllNotifications by viewModel.enableAllNotifications.collectAsState()
    val dmMode by viewModel.dmNotificationMode.collectAsState()
    val groupMode by viewModel.groupNotificationMode.collectAsState()
    val isRoomMentionEnabled by viewModel.isRoomMentionEnabled.collectAsState()
    val soundName by viewModel.notificationSoundName.collectAsState()

    var showDmDropdown by remember { mutableStateOf(false) }
    var showGroupDropdown by remember { mutableStateOf(false) }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                val name = ringtone?.getTitle(context) ?: "Custom Sound"
                viewModel.setNotificationSound(uri.toString(), name)
            } else {
                viewModel.setNotificationSound(null, "Default")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Global Toggles
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Device Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Notifications", fontWeight = FontWeight.Medium)
                        Text(
                            "Receive push notifications on this device", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enableAllNotifications,
                        onCheckedChange = { viewModel.setEnableAllNotifications(it) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Sound Picker
                ListItem(
                    headlineContent = { Text("Notification Sound", fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(soundName ?: "Default") },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound")
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                            }
                            ringtonePickerLauncher.launch(intent)
                        },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }

        // Native Matrix Notification Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Notification Rules",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Direct Messages
                Box {
                    ListItem(
                        headlineContent = { Text("Direct Messages", fontWeight = FontWeight.Medium) },
                        supportingContent = { 
                            Text(
                                when (dmMode) {
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.ALL_MESSAGES -> "All Messages"
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY -> "Mentions Only"
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE -> "Muted"
                                    else -> "Loading..."
                                }
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDmDropdown = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    DropdownMenu(
                        expanded = showDmDropdown,
                        onDismissRequest = { showDmDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Messages") },
                            onClick = { 
                                viewModel.setDmNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.ALL_MESSAGES)
                                showDmDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mentions Only") },
                            onClick = { 
                                viewModel.setDmNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY)
                                showDmDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mute") },
                            onClick = { 
                                viewModel.setDmNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE)
                                showDmDropdown = false
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Group Chats
                Box {
                    ListItem(
                        headlineContent = { Text("Group Chats", fontWeight = FontWeight.Medium) },
                        supportingContent = { 
                            Text(
                                when (groupMode) {
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.ALL_MESSAGES -> "All Messages"
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY -> "Mentions Only"
                                    org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE -> "Muted"
                                    else -> "Loading..."
                                }
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showGroupDropdown = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    DropdownMenu(
                        expanded = showGroupDropdown,
                        onDismissRequest = { showGroupDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Messages") },
                            onClick = { 
                                viewModel.setGroupNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.ALL_MESSAGES)
                                showGroupDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mentions Only") },
                            onClick = { 
                                viewModel.setGroupNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY)
                                showGroupDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mute") },
                            onClick = { 
                                viewModel.setGroupNotificationMode(org.matrix.rustcomponents.sdk.RoomNotificationMode.MUTE)
                                showGroupDropdown = false
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Room Mentions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notify on @room", fontWeight = FontWeight.Medium)
                        Text(
                            "Receive notifications when someone uses @room in a group", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isRoomMentionEnabled,
                        onCheckedChange = { viewModel.setRoomMentionEnabled(it) }
                    )
                }
            }
        }
    }
}
