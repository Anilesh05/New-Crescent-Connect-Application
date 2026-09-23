with open("app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt", "r") as f:
    content = f.read()

new_ui = """package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CrescentHeader
import com.example.ui.components.CrescentCard
import com.example.ui.dashboard.QuickAccessIcon
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassAdviserDashboard(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CrescentHeader(
                title = "Class Adviser Dashboard",
                subtitle = "MCA - II Year, Section A",
                avatarText = "AD"
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Welcome, Dr. Adviser 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Class Overview
                Text("Class Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CrescentCard(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Total Students", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Filled.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("64", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    }
                    CrescentCard(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Avg. Attendance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("84.2%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    }
                }

                // Quick Actions
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        QuickAccessIcon("Students", Icons.Filled.People, Color(0xFFEFF6FF), Color(0xFF2563EB))
                        QuickAccessIcon("Attendance", Icons.Filled.DateRange, Color(0xFFFFF7ED), Color(0xFFEA580C))
                        QuickAccessIcon("CR Mgmt", Icons.Filled.AdminPanelSettings, Color(0xFFFDF2F8), Color(0xFFDB2777))
                        QuickAccessIcon("Reports", Icons.Filled.Assessment, Color(0xFFFEFCE8), Color(0xFFCA8A04))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        QuickAccessIcon("Notices", Icons.Filled.Campaign, Color(0xFFF5F3FF), Color(0xFF7C3AED))
                        Spacer(modifier = Modifier.width((20).dp))
                        QuickAccessIcon("AI Assist", Icons.Filled.AutoAwesome, Color(0xFFF0FDFA), Color(0xFF0D9488))
                    }
                }

                // Insights
                Text("Recent Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Attendance Alert", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("5 students have dropped below 75% overall attendance this week.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("View Students", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt", "w") as f:
    f.write(new_ui)
