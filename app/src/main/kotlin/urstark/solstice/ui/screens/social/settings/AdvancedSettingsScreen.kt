package urstark.solstice.ui.screens.social.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import urstark.solstice.ui.screens.social.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val sharePresence by viewModel.sharePresence.collectAsState()
    val optimizeMediaQuality by viewModel.optimizeMediaQuality.collectAsState()
    val showMediaInTimeline by viewModel.showMediaInTimeline.collectAsState()

    var showMediaDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Presence & Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Presence",
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
                        Text("Share Presence Status", fontWeight = FontWeight.Medium)
                        Text(
                            "Let others see when you are online", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = sharePresence,
                        onCheckedChange = { viewModel.setSharePresence(it) }
                    )
                }
            }
        }

        // Media & Compression
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Media",
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
                        Text("Optimize Media Quality", fontWeight = FontWeight.Medium)
                        Text(
                            "Compress photos and videos before uploading to save data", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = optimizeMediaQuality,
                        onCheckedChange = { viewModel.setOptimizeMediaQuality(it) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Show Media in Timeline Dropdown
                Box {
                    ListItem(
                        headlineContent = { Text("Show Media in Timeline", fontWeight = FontWeight.Medium) },
                        supportingContent = { Text(showMediaInTimeline) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showMediaDropdown = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    DropdownMenu(
                        expanded = showMediaDropdown,
                        onDismissRequest = { showMediaDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Always") },
                            onClick = { 
                                viewModel.setShowMediaInTimeline("Always")
                                showMediaDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hide in private") },
                            onClick = { 
                                viewModel.setShowMediaInTimeline("Hide in private")
                                showMediaDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Always show") },
                            onClick = { 
                                viewModel.setShowMediaInTimeline("Always show")
                                showMediaDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}
