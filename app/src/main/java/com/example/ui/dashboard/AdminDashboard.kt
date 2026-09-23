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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrescentHeader
import com.example.ui.components.CrescentCard
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    modifier: Modifier = Modifier, 
    viewModel: UserDashboardViewModel,
    sessionManager: SessionManager,
    onNavigateToDigitalID: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
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

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Students", "Attendance", "Reports", "Profile")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.People, Icons.Filled.Checklist, Icons.Filled.BarChart, Icons.Filled.Person)
    
    Scaffold(
        modifier = modifier,
        topBar = {
            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "AD"
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
                0 -> AdminHomeTab(uiState)
                4 -> ProfileTab(
                    user = uiState.user,
                    onNavigateToDigitalID = onNavigateToDigitalID,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    onThemeToggle = { showThemeDialog = true }
                )
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(items[selectedItem]) }
            }
        }
    }
}

@Composable
fun AdminHomeTab(uiState: UserDashboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = "Welcome, ${uiState.user?.name ?: "Admin"} 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "System Administration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Access Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminActionCard(modifier = Modifier.weight(1f), title = "Students", value = "Manage", icon = Icons.Filled.People, iconColor = Color(0xFF2563EB), bgColor = Color(0xFFEFF6FF))
                AdminActionCard(modifier = Modifier.weight(1f), title = "Staff", value = "Manage", icon = Icons.Filled.Badge, iconColor = Color(0xFF16A34A), bgColor = Color(0xFFF0FDF4))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminActionCard(modifier = Modifier.weight(1f), title = "Classes", value = "Manage", icon = Icons.Filled.Class, iconColor = Color(0xFFDC2626), bgColor = Color(0xFFFEF2F2))
                AdminActionCard(modifier = Modifier.weight(1f), title = "Subjects", value = "Manage", icon = Icons.Filled.MenuBook, iconColor = Color(0xFFCA8A04), bgColor = Color(0xFFFEFCE8))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminActionCard(modifier = Modifier.weight(1f), title = "CR Perms", value = "Manage", icon = Icons.Filled.Security, iconColor = Color(0xFF0D9488), bgColor = Color(0xFFF0FDFA))
                AdminActionCard(modifier = Modifier.weight(1f), title = "Audit Logs", value = "View", icon = Icons.Filled.History, iconColor = Color(0xFF7C3AED), bgColor = Color(0xFFF5F3FF))
            }
        }
    }
}

@Composable
fun AdminActionCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector, iconColor: Color, bgColor: Color) {
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
