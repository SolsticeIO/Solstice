/*
 * Solstice (2026)
 * © Stark — github.com/urstark
 * 
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */
package urstark.solstice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import urstark.solstice.ui.screens.social.SocialLoginScreen
import urstark.solstice.ui.screens.social.SocialViewModel

@Composable
fun SocialScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.matrixManager.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        SocialLoginScreen(matrixManager = viewModel.matrixManager)
    } else {
        SocialMainContent(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMainContent(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Slim, borderless, pill-shaped search bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .height(50.dp)
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Groups") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("DM") }
            )
        }

        // Content
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (selectedTab) {
                0 -> Text("Groups List (Matrix Integration Pending)")
                1 -> Text("Direct Messages List (Matrix Integration Pending)")
            }
        }
    }
}
