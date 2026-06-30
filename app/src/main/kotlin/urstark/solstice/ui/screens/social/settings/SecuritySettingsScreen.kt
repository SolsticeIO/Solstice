package urstark.solstice.ui.screens.social.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import urstark.solstice.ui.screens.social.SocialViewModel

@Composable
fun SecuritySettingsScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val threePids by viewModel.threePids.collectAsState()
    val identityServer by viewModel.identityServer.collectAsState()
    val ignoredUsers by viewModel.ignoredUsers.collectAsState()
    var newIdentityServer by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(identityServer) {
        newIdentityServer = identityServer ?: ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Blocked Users Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Blocked Users",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (ignoredUsers.isEmpty()) {
                    Text(
                        text = "You haven't blocked any users yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ignoredUsers.forEach { userId ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userId,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.unblockUser(userId) }
                            ) {
                                Text("Unblock", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Screen Lock Card
        val isScreenLockEnabled by viewModel.isScreenLockEnabled.collectAsState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Application Security",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Screen Lock", fontWeight = FontWeight.Medium)
                        Text(
                            "Use system biometric authentication to unlock chats", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isScreenLockEnabled,
                        onCheckedChange = { viewModel.setScreenLockEnabled(it) }
                    )
                }
            }
        }

        // Privacy Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Discovery & Privacy",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Email Addresses", 
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val emails = threePids.filter { it.medium == "email" }
                if (emails.isEmpty()) {
                    Text(
                        text = "No email addresses linked to this account.", 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    emails.forEach { email ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(email.address, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Linked", 
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Phone Numbers", 
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val phones = threePids.filter { it.medium == "msisdn" }
                if (phones.isEmpty()) {
                    Text(
                        text = "No phone numbers linked to this account.", 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    phones.forEach { phone ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween, 
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(phone.address, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Linked", 
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Identity Server Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Identity Server",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "An identity server is used to discover which of your contacts are already on Matrix.", 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = newIdentityServer,
                    onValueChange = { newIdentityServer = it },
                    label = { Text("Identity Server URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.End, 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (identityServer != null) {
                        Button(
                            onClick = { 
                                viewModel.setIdentityServer(null)
                                android.widget.Toast.makeText(context, "Disconnected identity server", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Disconnect")
                        }
                    }
                    Button(
                        onClick = { 
                            if (newIdentityServer.isNotBlank()) {
                                viewModel.setIdentityServer(newIdentityServer)
                                android.widget.Toast.makeText(context, "Identity server saved", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        // Integrations Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Integrations",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Integration managers (like Dimension or Scalar) let you add widgets, bots, and sticker packs to rooms.", 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Integration Manager", fontWeight = FontWeight.Medium)
                    Switch(checked = false, onCheckedChange = {})
                }
            }
        }
    }
}
