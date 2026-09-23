package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CrescentHeader
import com.example.ui.components.CrescentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboard(
    modifier: Modifier = Modifier,
    viewModel: StudentDashboardViewModel, 
    attendanceViewModel: StudentAttendanceViewModel, 
    crAttendanceViewModel: CRAttendanceViewModel? = null,
    onNavigateToDigitalID: () -> Unit = {},
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToCourseChat: (String, String, String) -> Unit = {_,_,_ ->},
    onLogout: () -> Unit = {},
    sessionManager: SessionManager
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    
    val uiState by viewModel.uiState.collectAsState()
    val isCR = uiState.user?.role == com.example.domain.model.Role.CR
    val items = if (isCR) listOf("Home", "Attendance", "Academics", "Messages", "CR Duty", "Profile") else listOf("Home", "Attendance", "Academics", "Messages", "Profile")
    val icons = if (isCR) listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.MenuBook, Icons.Filled.Message, Icons.Filled.Security, Icons.Filled.Person) else listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.MenuBook, Icons.Filled.Message, Icons.Filled.Person)

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

    Scaffold(
        modifier = modifier,
        topBar = {
            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "AN",
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onPrimary)
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
                        label = { Text(item, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
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
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when {
                    selectedItem == 0 -> StudentHomeTab(
                            uiState = uiState,
                            attendanceState = attendanceViewModel.uiState.collectAsState().value,
                            onNavigateToAiAssistant = onNavigateToAiAssistant, 
                            onNavigateToDigitalID = onNavigateToDigitalID
                        )
                    selectedItem == 1 -> StudentAttendanceTab(viewModel = attendanceViewModel)
                    selectedItem == 2 -> StudentAcademicsTab()
                    selectedItem == 3 -> StudentMessagesTab(uiState = uiState, onNavigateToCourseChat = onNavigateToCourseChat)
                    selectedItem == 4 && isCR -> {
                        if (crAttendanceViewModel != null) CRAttendanceTab(crAttendanceViewModel) else Text("Loading CR...")
                    }
                    selectedItem == items.lastIndex -> ProfileTab(
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
}

@Composable
fun StudentHomeTab(
    uiState: StudentDashboardUiState,
    attendanceState: StudentAttendanceUiState? = null,
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToDigitalID: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text("Hello, ${uiState.user?.name?.split(" ")?.firstOrNull() ?: "Student"} 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${uiState.user?.programme ?: "MCA"} - ${uiState.user?.semester ?: "II Year"} | Section ${uiState.user?.section ?: "A"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Attendance Card
        CrescentCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Overall Attendance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    val progress = (attendanceState?.overallPercentage ?: 0f) / 100f
                    val percentageText = kotlin.math.round(attendanceState?.overallPercentage ?: 0f).toInt().toString()
                    val isShortage = (attendanceState?.overallPercentage ?: 0f) < 75f
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = if (isShortage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("$percentageText%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = if(isShortage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    val statusText1 = if ((attendanceState?.overallPercentage ?: 0f) >= 75f) "You are above" else "You are below"
                    val statusColor = if ((attendanceState?.overallPercentage ?: 0f) >= 75f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    Text(statusText1, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("the required 75%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = statusColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("View Details", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Access
        Text("Quick Access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickAccessIcon("Attendance", Icons.Filled.DateRange, Color(0xFFFFF7ED), Color(0xFFEA580C))
                QuickAccessIcon("Marks", Icons.Filled.CheckCircle, Color(0xFFFEF2F2), Color(0xFFDC2626))
                QuickAccessIcon("Timetable", Icons.Filled.Event, Color(0xFFF0FDF4), Color(0xFF16A34A))
                QuickAccessIcon("Materials", Icons.Filled.MenuBook, Color(0xFFFEFCE8), Color(0xFFCA8A04))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickAccessIcon("Assignments", Icons.Filled.Assignment, Color(0xFFF5F3FF), Color(0xFF7C3AED))
                QuickAccessIcon("Messages", Icons.Filled.Message, Color(0xFFEFF6FF), Color(0xFF2563EB))
                QuickAccessIcon("AI Assistant", Icons.Filled.AutoAwesome, Color(0xFFFDF2F8), Color(0xFFDB2777), onClick = onNavigateToAiAssistant)
                QuickAccessIcon("Digital ID", Icons.Filled.Badge, Color(0xFFF0FDFA), Color(0xFF0D9488), onClick = onNavigateToDigitalID)
            }
        }

        
        // Recent Announcements
        Text("Recent Announcements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Internal Assessment Update", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("DBMS • 30 Aug", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Please note that the internal assessment for Unit 3 has been postponed to next week.", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        // Upcoming
        Text("Enrolled Courses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (uiState.enrolledCourses.isEmpty()) {
            Text("No courses enrolled.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            uiState.enrolledCourses.forEach { course ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(course.subjectName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(course.subjectCode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickAccessIcon(label: String, icon: ImageVector, bgColor: Color, iconColor: Color, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable(onClick = onClick)) {
        Box(
            modifier = Modifier.size(56.dp).background(bgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
    }
}

@Composable
fun StudentMessagesTab(uiState: StudentDashboardUiState, onNavigateToCourseChat: (String, String, String) -> Unit = {_,_,_ ->}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Messages", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        uiState.enrolledCourses.forEach { course ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = { onNavigateToCourseChat(course.id, course.subjectName, course.subjectCode) }),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(course.subjectName.take(2).uppercase(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(course.subjectName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Course Chat Group", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }
        }
    }
}
