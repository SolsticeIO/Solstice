package urstark.solstice.ui.screens.social.settings

import android.app.Activity
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import urstark.solstice.ui.screens.social.SocialViewModel

@Composable
fun EncryptionSettingsScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    
    val prefs = remember { context.getSharedPreferences("social_prefs", Context.MODE_PRIVATE) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    var newlyGeneratedKey by remember { mutableStateOf<String?>(null) }
    var isResettingKey by remember { mutableStateOf(false) }
    var showViewSavedKeyDialog by remember { mutableStateOf(false) }
    var savedKeyToDisplay by remember { mutableStateOf<String?>(null) }

    val currentDeviceId = viewModel.matrixManager.client?.deviceId() ?: "Unknown"
    val isScreenLockEnabled by viewModel.isScreenLockEnabled.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Secure Backup Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Secure Backup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your chats are automatically backed up with end-to-end encryption on the homeserver. To restore this backup and decrypt your messages on a new device, you will need your recovery key.", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = { showResetConfirmation = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset / Generate Recovery Key")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val savedKey = prefs.getString("recovery_key", null)
                        
                        fun displayKey() {
                            savedKeyToDisplay = savedKey ?: "No locally saved recovery key found. You can generate a new one above."
                            showViewSavedKeyDialog = true
                        }

                        if (isScreenLockEnabled) {
                            val activity = context as? Activity
                            if (activity != null) {
                                try {
                                    val biometricPrompt = BiometricPrompt.Builder(context)
                                        .setTitle("View Recovery Key")
                                        .setSubtitle("Confirm your identity to view the recovery key")
                                        .setNegativeButton("Cancel", context.mainExecutor) { _, _ -> }
                                        .build()
                                        
                                    biometricPrompt.authenticate(
                                        CancellationSignal(),
                                        context.mainExecutor,
                                        object : BiometricPrompt.AuthenticationCallback() {
                                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                                                super.onAuthenticationSucceeded(result)
                                                displayKey()
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    displayKey()
                                }
                            } else {
                                displayKey()
                            }
                        } else {
                            displayKey()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Saved Recovery Key")
                }
            }
        }

        // Encryption Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Encryption Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                ListItem(
                    headlineContent = { Text("Device Session ID") },
                    supportingContent = { Text(currentDeviceId) },
                    leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { 
                        scope.launch {
                            try {
                                viewModel.matrixManager.client?.encryption()?.disableRecovery()
                                prefs.edit().remove("recovery_key").apply()
                                android.widget.Toast.makeText(context, "Secure Backup Disabled", android.widget.Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Failed to disable backup: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Disable Secure Backup")
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetConfirmation) {
            AlertDialog(
                onDismissRequest = { if (!isResettingKey) showResetConfirmation = false },
                title = { Text("Reset Recovery Key?") },
                text = { Text("This will invalidate your old recovery key and generate a new one. You MUST save the new key safely, or you will lose access to encrypted messages if you log out.") },
                confirmButton = {
                    Button(
                        onClick = {
                            isResettingKey = true
                            scope.launch {
                                try {
                                    val newKey = viewModel.matrixManager.client?.encryption()?.resetRecoveryKey()
                                    if (newKey != null) {
                                        prefs.edit().putString("recovery_key", newKey).apply()
                                    }
                                    newlyGeneratedKey = newKey
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Reset failed: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                } finally {
                                    isResettingKey = false
                                    showResetConfirmation = false
                                }
                            }
                        }
                    ) {
                        if (isResettingKey) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Generate New Key")
                        }
                    }
                },
                dismissButton = {
                    if (!isResettingKey) {
                        TextButton(onClick = { showResetConfirmation = false }) { Text("Cancel") }
                    }
                }
            )
        }

        // New Key Display Dialog
        if (newlyGeneratedKey != null) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("New Recovery Key", color = MaterialTheme.colorScheme.error) },
                text = {
                    Column {
                        Text("SAVE THIS IMMEDIATELY! We will not show it again.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant, 
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = newlyGeneratedKey!!, 
                                modifier = Modifier.padding(16.dp), 
                                style = MaterialTheme.typography.bodyLarge, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                clipboardManager.setText(buildAnnotatedString { append(newlyGeneratedKey!!) })
                                android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy to Clipboard")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { newlyGeneratedKey = null },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("I HAVE SAVED IT SECURELY")
                    }
                }
            )
        }

        // View Saved Key Dialog
        if (showViewSavedKeyDialog && savedKeyToDisplay != null) {
            AlertDialog(
                onDismissRequest = { showViewSavedKeyDialog = false },
                title = { Text("Saved Recovery Key") },
                text = {
                    Column {
                        Text("This is the recovery key saved on this device:")
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant, 
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = savedKeyToDisplay!!, 
                                modifier = Modifier.padding(16.dp), 
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (savedKeyToDisplay!!.startsWith("emsk") || savedKeyToDisplay!!.length > 40) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    clipboardManager.setText(buildAnnotatedString { append(savedKeyToDisplay!!) })
                                    android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy to Clipboard")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showViewSavedKeyDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
