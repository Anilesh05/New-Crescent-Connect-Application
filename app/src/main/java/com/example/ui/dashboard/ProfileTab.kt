package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrescentCard

@Composable
fun ProfileTab(
    user: com.example.domain.model.User?,
    onNavigateToDigitalID: () -> Unit = {},
    onLogout: () -> Unit = {}, onThemeToggle: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Picture
        Box(
            modifier = Modifier.size(120.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(user?.name?.take(2)?.uppercase() ?: "AN", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Black)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(user?.name ?: "Student", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (user?.role == com.example.domain.model.Role.STAFF || user?.role == com.example.domain.model.Role.CLASS_ADVISER || user?.role == com.example.domain.model.Role.ADMIN) {
                Text("${user.department ?: "Department"} • ${user.designation ?: "Staff"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text("${user?.programme ?: ""} • ${user?.semester ?: ""} • Section ${user?.section ?: ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(user?.email ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMenuRow(Icons.Filled.Badge, "Digital ID", Color(0xFF0D9488), onClick = onNavigateToDigitalID)
            if (user?.role == com.example.domain.model.Role.STUDENT || user?.role == com.example.domain.model.Role.CR) {
                ProfileMenuRow(Icons.Filled.Description, "Resume", Color(0xFFCA8A04))
                ProfileMenuRow(Icons.Filled.Stars, "Skills & Certifications", Color(0xFFEA580C))
                ProfileMenuRow(Icons.Filled.Folder, "Projects", Color(0xFF2563EB))
                ProfileMenuRow(Icons.Filled.School, "Academic Profile", Color(0xFF7C3AED))
            }
            ProfileMenuRow(Icons.Filled.Palette, "Toggle Theme", Color(0xFF8B5CF6), onClick = onThemeToggle)
            ProfileMenuRow(Icons.Filled.Settings, "Settings", Color(0xFF4B5563))
            ProfileMenuRow(Icons.Filled.Logout, "Logout", MaterialTheme.colorScheme.error, onClick = onLogout)
        }

    }
}

@Composable
fun ProfileMenuRow(icon: ImageVector, title: String, iconColor: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (icon == Icons.Filled.Logout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
