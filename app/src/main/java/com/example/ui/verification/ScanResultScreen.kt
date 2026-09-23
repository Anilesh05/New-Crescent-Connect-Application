package com.example.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.ui.dashboard.QrVerificationToken
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    payload: String,
    userRepository: UserRepository,
    onBack: () -> Unit
) {
    var scannedUser by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(payload) {
        try {
            val token = Json.decodeFromString<QrVerificationToken>(payload)
            
            // Check basic structure and expiry
            if (token.type != "CRESCENT_ID") {
                errorMessage = "Invalid QR code format."
            } else if (System.currentTimeMillis() > token.expiresAt) {
                errorMessage = "Verification Token Expired."
            } else {
                // Fetch user directly from Repository for verification
                val user = userRepository.getUserById(token.userId)
                if (user != null) {
                    if (user.idVersion != token.version) {
                        errorMessage = "ID Version mismatch. Token may be stale."
                    } else if (user.status == "REVOKED") {
                        errorMessage = "ID REVOKED"
                    } else if (!user.isActive) {
                        errorMessage = "User is not active."
                    } else {
                        scannedUser = user
                    }
                } else {
                    errorMessage = "User not found in system."
                }
            }
        } catch (e: Exception) {
            errorMessage = "Failed to parse QR code."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Result", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                ErrorResultCard(errorMessage = errorMessage!!)
            } else if (scannedUser != null) {
                SuccessResultCard(user = scannedUser!!)
            }
        }
    }
}

@Composable
fun SuccessResultCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Verified", tint = Color(0xFF0D9488), modifier = Modifier.size(64.dp))
            Text("ID VERIFIED", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF0D9488))
            
            Box(
                modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.take(2).uppercase(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Black)
            }
            
            Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(user.role.name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            if (user.registerNumber != null) {
                DetailRow("Register No", user.registerNumber!!)
            } else if (user.designation != null) {
                DetailRow("Staff ID", user.id) // Using ID as staff ID for now
                DetailRow("Designation", user.designation!!)
            }
            
            if (user.department != null) {
                DetailRow("Department", user.department!!)
            }
            
            DetailRow("Status", user.status)
            DetailRow("Verified", SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()))
        }
    }
}

@Composable
fun ErrorResultCard(errorMessage: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
            Text("INVALID ID", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
            Text(errorMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
