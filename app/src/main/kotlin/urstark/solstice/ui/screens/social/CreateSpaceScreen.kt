package urstark.solstice.ui.screens.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.rustcomponents.sdk.CreateRoomParameters
import org.matrix.rustcomponents.sdk.RoomPreset
import org.matrix.rustcomponents.sdk.RoomVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSpaceScreen(
    navController: NavController,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var spaceName by remember { mutableStateOf("") }
    var spaceTopic by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Space") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Avatar Selector
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Space Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Select Avatar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = spaceName,
                onValueChange = { spaceName = it },
                label = { Text("Space Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = spaceTopic,
                onValueChange = { spaceTopic = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Public/Private Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Public Space", 
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Anyone can find and join this space", 
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (spaceName.isBlank()) {
                        errorMessage = "Space name cannot be empty"
                        return@Button
                    }
                    isCreating = true
                    errorMessage = null
                    
                    scope.launch {
                        try {
                            val client = viewModel.matrixManager.client
                            if (client != null) {
                                var avatarMxc: String? = null
                                
                                // Upload avatar if selected
                                if (selectedImageUri != null) {
                                    val bytes = withContext(Dispatchers.IO) {
                                        context.contentResolver.openInputStream(selectedImageUri!!)?.readBytes()
                                    }
                                    if (bytes != null) {
                                        avatarMxc = client.uploadMedia("image/jpeg", bytes, null)
                                    }
                                }

                                // Use Ktor API helper to create Space
                                val spaceId = viewModel.createSpace(spaceName, isPublic)
                                if (spaceId != null) {
                                    withContext(Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Space created successfully!", android.widget.Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                } else {
                                    errorMessage = "Failed to create space"
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errorMessage = "Failed to create space: ${e.localizedMessage}"
                        } finally {
                            isCreating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isCreating && spaceName.isNotBlank()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Space", fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
