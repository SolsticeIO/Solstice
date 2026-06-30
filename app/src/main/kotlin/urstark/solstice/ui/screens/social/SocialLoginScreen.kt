package urstark.solstice.ui.screens.social

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import urstark.solstice.matrix.MatrixClientManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialLoginScreen(
    matrixManager: MatrixClientManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    val isLoading by matrixManager.isLoading.collectAsState()
    val error by matrixManager.error.collectAsState()
    
    var showLoginForm by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
    
    var homeserver by remember { mutableStateOf("matrix.org") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }

    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 80.dp

    fun launchWeb(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        try {
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (showLoginForm) {
                // Back Button & Login Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showLoginForm = false }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Welcome Screen"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }

            Text(
                text = "Solstice Social",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Secured by Matrix. Decentralized & encrypted.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )

            // Error message
            if (error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (!showLoginForm) {
                // Welcome Screen Buttons
                Button(
                    onClick = { showLoginForm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { launchWeb("https://matrix.org/signup") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { launchWeb("https://app.element.io/#/forgot-password") }
                ) {
                    Text("Forgot Password?", fontSize = 14.sp)
                }
            } else {
                // Credential Form
                // Custom Homeserver Dropdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdvanced = !showAdvanced }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Advanced Options (Homeserver)",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (showAdvanced) "▲" else "▼",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Advanced options (Homeserver URL)
                AnimatedVisibility(visible = showAdvanced) {
                    OutlinedTextField(
                        value = homeserver,
                        onValueChange = { homeserver = it },
                        label = { Text("Homeserver URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username / Matrix ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        scope.launch {
                            matrixManager.login(homeserver, username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
                ) {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(bottomPadding + 40.dp))
        }

        if (isLoading) {
            Surface(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Signing you in...", 
                                style = MaterialTheme.typography.bodyMedium, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
