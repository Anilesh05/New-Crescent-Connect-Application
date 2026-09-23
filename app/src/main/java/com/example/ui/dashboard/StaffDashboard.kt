package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrescentHeader
import com.example.ui.components.CrescentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboard(
    modifier: Modifier = Modifier, 
    viewModel: StaffDashboardViewModel,
    attendanceViewModel: StaffAttendanceViewModel, 
    sessionManager: SessionManager,
    onNavigateToScanID: () -> Unit = {},
    onNavigateToDigitalID: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentTheme by sessionManager.themeFlow.collectAsState(initial = "system")

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = { newTheme ->
                coroutineScope.launch { sessionManager.saveTheme(newTheme) }
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    val items = listOf("Home", "Classes", "Attendance", "Messages", "Profile")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Class, Icons.Filled.Checklist, Icons.Filled.Message, Icons.Filled.Person)

    Scaffold(
        modifier = modifier,
        topBar = {
            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = "DK",
                actions = {
                    IconButton(onClick = onNavigateToScanID) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan ID", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            when (selectedItem) {
                2 -> StaffAttendanceTab(viewModel = attendanceViewModel)
                0 -> StaffHomeTab(uiState)
                4 -> ProfileTab(
                    user = uiState.user,
                    onNavigateToDigitalID = onNavigateToDigitalID,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    onThemeToggle = { showThemeDialog = true }
                )
            }
        }
    }
}

@Composable
fun StaffHomeTab(uiState: StaffDashboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = "Welcome, ${uiState.user?.name ?: "Staff"} 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${uiState.user?.designation ?: "Staff"} - ${uiState.user?.department ?: "Department"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Access Cards Grid
        Text("Quick Access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "My Classes", value = "4", icon = Icons.Filled.DateRange, iconColor = Color(0xFF2563EB), bgColor = Color(0xFFEFF6FF))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Students", value = "72", icon = Icons.Filled.People, iconColor = Color(0xFF16A34A), bgColor = Color(0xFFF0FDF4))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "Attendance", value = "Mark & View", icon = Icons.Filled.Checklist, iconColor = Color(0xFFDC2626), bgColor = Color(0xFFFEF2F2))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Marks", value = "Enter & View", icon = Icons.Filled.Edit, iconColor = Color(0xFFCA8A04), bgColor = Color(0xFFFEFCE8))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "Study Materials", value = "Upload", icon = Icons.Filled.MenuBook, iconColor = Color(0xFF0D9488), bgColor = Color(0xFFF0FDFA))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Announcements", value = "Create", icon = Icons.Filled.Campaign, iconColor = Color(0xFF7C3AED), bgColor = Color(0xFFF5F3FF))
            }
        }
        
        Text("Today's Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("DBMS - MCA II Year", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("10:00 AM - 11:40 AM | Block A - 302", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Pending", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Data Structures - B.Tech I Year", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("02:00 PM - 03:40 PM | Block B - 104", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StaffActionCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, iconColor: Color, bgColor: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(bgColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
