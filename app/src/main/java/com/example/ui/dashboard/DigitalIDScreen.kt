package com.example.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrescentButton
import com.example.ui.components.CrescentStatusBadge
import com.example.ui.components.BadgeStatus
import androidx.compose.ui.graphics.ImageBitmap
import com.example.ui.components.QrCodeHelper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class QrVerificationToken(
    val type: String = "CRESCENT_ID",
    val userId: String,
    val role: String,
    val version: Int,
    val issuedAt: Long,
    val expiresAt: Long,
    val signature: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalIDScreen(viewModel: DigitalIDViewModel, onBack: () -> Unit = {}) {
    val userState by viewModel.user.collectAsState()
    val user = userState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digital ID", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Box(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "B.S. Abdur Rahman Crescent Institute\nof Science and Technology",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Picture
                    Box(
                        modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.take(2).uppercase(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user.role.name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Details
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (user.role == com.example.domain.model.Role.STUDENT || user.role == com.example.domain.model.Role.CR) {
                            IDDetailRow("Register Number", user.registerNumber ?: "-")
                            IDDetailRow("Programme", user.programme ?: "-")
                            IDDetailRow("Department", user.department ?: "-")
                            IDDetailRow("Semester / Section", "${user.semester ?: "-"} / ${user.section ?: "-"}")
                            IDDetailRow("Academic Year", user.academicYear ?: "-")
                        } else {
                            IDDetailRow("Staff ID", user.registerNumber ?: user.id)
                            IDDetailRow("Department", user.department ?: "-")
                            IDDetailRow("Designation", user.designation ?: "-")
                            IDDetailRow("Email", user.email)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))


                    // Generate Token payload
                    val token = QrVerificationToken(
                        userId = user.id,
                        role = user.role.name,
                        version = user.idVersion,
                        issuedAt = System.currentTimeMillis(),
                        expiresAt = System.currentTimeMillis() + (1000 * 60 * 10), // 10 minutes expiry
                        signature = "mock_signature_for_${user.id}"
                    )
                    
                    val tokenJson = Json.encodeToString(token)
                    val qrBitmap = QrCodeHelper.generateQrCode(tokenJson)

                    Box(
                        modifier = Modifier.size(160.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(bitmap = qrBitmap, contentDescription = "Verification QR Code", modifier = Modifier.fillMaxSize())
                        } else {
                            Icon(Icons.Filled.QrCode2, contentDescription = "QR Code", modifier = Modifier.fillMaxSize(), tint = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Valid for 10 minutes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)


                    Spacer(modifier = Modifier.height(24.dp))
                    if (user.status == "ACTIVE") {
                        CrescentStatusBadge("ACTIVE", BadgeStatus.SUCCESS)
                    } else if (user.status == "REVOKED") {
                        CrescentStatusBadge("REVOKED", BadgeStatus.ERROR)
                    } else {
                        CrescentStatusBadge(user.status, BadgeStatus.WARNING)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CrescentButton("Verify ID", onClick = { }, modifier = Modifier.weight(1f))
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("Generate PDF", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun IDDetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}
