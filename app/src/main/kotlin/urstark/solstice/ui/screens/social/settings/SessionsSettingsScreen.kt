package urstark.solstice.ui.screens.social.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import urstark.solstice.ui.screens.social.SocialViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionsSettingsScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsState()
    val isLoading by viewModel.isLoadingSettings.collectAsState()
    val currentDeviceId = viewModel.matrixManager.client?.deviceId()
    val isVerified by viewModel.isSessionVerified.collectAsState()
    val scope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Current Session
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Current Session",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                val currentDevice = devices.find { it.device_id == currentDeviceId }
                
                ListItem(
                    headlineContent = { Text(currentDevice?.display_name ?: currentDeviceId ?: "Current Device") },
                    supportingContent = { 
                        Column {
                            Text("ID: ${currentDeviceId ?: "Unknown"}")
                            if (currentDevice?.last_seen_ip != null) {
                                Text("IP: ${currentDevice.last_seen_ip}")
                            }
                        }
                    },
                    leadingContent = { 
                        Icon(
                            imageVector = Icons.Default.Devices, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) 
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isVerified) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                            } else {
                                Icon(Icons.Default.Warning, contentDescription = "Unverified", tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unverified", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }

        // Other Sessions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val otherDevices = devices.filter { it.device_id != currentDeviceId }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Other Active Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (otherDevices.isNotEmpty()) {
                        var isSigningOutAll by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = {
                                isSigningOutAll = true
                                scope.launch {
                                    try {
                                        viewModel.signOutOtherSessions()
                                    } finally {
                                        isSigningOutAll = false
                                    }
                                }
                            },
                            enabled = !isSigningOutAll
                        ) {
                            if (isSigningOutAll) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text("Sign Out All", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading && devices.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (otherDevices.isEmpty()) {
                        Text(
                            text = "No other active sessions.", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        otherDevices.forEachIndexed { index, device ->
                            var isSigningOut by remember { mutableStateOf(false) }
                             ListItem(
                                headlineContent = { Text(device.display_name ?: device.device_id) },
                                supportingContent = { 
                                    val dateStr = device.last_seen_ts?.let { dateFormat.format(Date(it)) } ?: "Unknown date"
                                    val ip = device.last_seen_ip ?: "Unknown IP"
                                    Text("Last seen: $dateStr\nIP: $ip")
                                },
                                leadingContent = { 
                                    Icon(
                                        imageVector = Icons.Default.DeviceUnknown, 
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp)
                                    ) 
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            isSigningOut = true
                                            scope.launch {
                                                try {
                                                    viewModel.signOutSession(device.device_id)
                                                } finally {
                                                    isSigningOut = false
                                                }
                                            }
                                        },
                                        enabled = !isSigningOut
                                    ) {
                                        if (isSigningOut) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.DeleteForever, 
                                                contentDescription = "Sign out of session",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            if (index < otherDevices.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}
