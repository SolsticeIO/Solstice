package urstark.solstice.ui.screens.social

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import urstark.solstice.ui.screens.social.settings.AccountSettingsScreen
import urstark.solstice.ui.screens.social.settings.SessionsSettingsScreen
import urstark.solstice.ui.screens.social.settings.NotificationsSettingsScreen
import urstark.solstice.ui.screens.social.settings.SecuritySettingsScreen
import urstark.solstice.ui.screens.social.settings.EncryptionSettingsScreen
import urstark.solstice.ui.screens.social.settings.AdvancedSettingsScreen

enum class SettingsPage(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Main("Settings", Icons.Default.Settings),
    Account("Account", Icons.Default.AccountCircle),
    Sessions("Sessions", Icons.Default.Devices),
    Notifications("Notifications", Icons.Default.Notifications),
    Security("Security & Privacy", Icons.Default.Security),
    Encryption("Encryption", Icons.Default.Lock),
    Advanced("Advanced Settings", Icons.Default.Tune)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSettingsScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    var currentPage by remember { mutableStateOf(SettingsPage.Main) }
    
    LaunchedEffect(Unit) {
        viewModel.loadSettingsData()
    }

    BackHandler(enabled = currentPage != SettingsPage.Main) {
        currentPage = SettingsPage.Main
    }

    BoxWithConstraints {
        val isTablet = maxWidth > 600.dp
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (currentPage == SettingsPage.Main || isTablet) "Settings" else "Settings: ${currentPage.title}") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentPage == SettingsPage.Main || isTablet) {
                                navController.navigateUp()
                            } else {
                                currentPage = SettingsPage.Main
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (isTablet) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            SettingsMainList(
                                currentPage = currentPage,
                                onNavigate = { currentPage = it }
                            )
                        }
                        VerticalDivider()
                        Box(modifier = Modifier.weight(2f).fillMaxHeight()) {
                            when (currentPage) {
                                SettingsPage.Main -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select a setting") }
                                SettingsPage.Account -> AccountSettingsScreen(viewModel)
                                SettingsPage.Sessions -> SessionsSettingsScreen(viewModel)
                                SettingsPage.Notifications -> NotificationsSettingsScreen(viewModel)
                                SettingsPage.Security -> SecuritySettingsScreen(viewModel)
                                SettingsPage.Encryption -> EncryptionSettingsScreen(viewModel)
                                SettingsPage.Advanced -> AdvancedSettingsScreen(viewModel)
                            }
                        }
                    }
                } else {
                    when (currentPage) {
                        SettingsPage.Main -> SettingsMainList(currentPage = currentPage, onNavigate = { currentPage = it })
                        SettingsPage.Account -> AccountSettingsScreen(viewModel)
                        SettingsPage.Sessions -> SessionsSettingsScreen(viewModel)
                        SettingsPage.Notifications -> NotificationsSettingsScreen(viewModel)
                        SettingsPage.Security -> SecuritySettingsScreen(viewModel)
                        SettingsPage.Encryption -> EncryptionSettingsScreen(viewModel)
                        SettingsPage.Advanced -> AdvancedSettingsScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsMainList(currentPage: SettingsPage, onNavigate: (SettingsPage) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val items = listOf(
            Triple(SettingsPage.Account, "Manage your profile picture, display name, and login credentials.", Icons.Default.AccountCircle),
            Triple(SettingsPage.Sessions, "View and manage other devices logged into your account.", Icons.Default.Devices),
            Triple(SettingsPage.Notifications, "Configure push notifications and server notification rules.", Icons.Default.Notifications),
            Triple(SettingsPage.Security, "Manage blocked users, privacy, and screen lock.", Icons.Default.Security),
            Triple(SettingsPage.Encryption, "Manage secure backup, recovery keys, and encryption status.", Icons.Default.Lock),
            Triple(SettingsPage.Advanced, "Configure presence, media optimization, and advanced timeline settings.", Icons.Default.Tune)
        )

        items.forEach { (page, description, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(page) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentPage == page) 
                        MaterialTheme.colorScheme.surfaceVariant 
                    else 
                        MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (currentPage == page)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (currentPage == page)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
