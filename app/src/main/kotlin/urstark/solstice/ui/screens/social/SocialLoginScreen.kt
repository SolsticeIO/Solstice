package urstark.solstice.ui.screens.social

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    val isLoading by matrixManager.isLoading.collectAsState()
    val error by matrixManager.error.collectAsState()
    
    var isSignUp by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }
    
    var homeserver by remember { mutableStateOf("matrix.org") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Solstice Social",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Secured by Matrix. Decentralized & encrypted.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        
        // Tabs
        TabRow(
            selectedTabIndex = if (isSignUp) 1 else 0,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Tab(
                selected = !isSignUp,
                onClick = { isSignUp = false },
                text = { Text("Log In") }
            )
            Tab(
                selected = isSignUp,
                onClick = { isSignUp = true },
                text = { Text("Sign Up") }
            )
        }

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
                text = "Advanced Options",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (showAdvanced) "▲" else "▼",
                color = MaterialTheme.colorScheme.primary
            )
        }

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

        if (isSignUp) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional for custom servers)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
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
                    if (isSignUp) {
                        matrixManager.register(homeserver, username, password, email.takeIf { it.isNotBlank() })
                    } else {
                        matrixManager.login(homeserver, username, password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(if (isSignUp) "Create Account" else "Log In", fontSize = 16.sp)
            }
        }

        if (!isSignUp) {
            TextButton(
                onClick = {
                    scope.launch {
                        matrixManager.requestPasswordReset(homeserver, username)
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Forgot Password?")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp)) // padding for scrolling
    }
}
