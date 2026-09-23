with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "r") as f:
    content = f.read()

# Update call
old_call = """                    selectedItem == 0 -> StudentHomeTab(
                            uiState = uiState,
                            onNavigateToAiAssistant = onNavigateToAiAssistant, 
                            onNavigateToDigitalID = onNavigateToDigitalID
                        )"""
new_call = """                    selectedItem == 0 -> StudentHomeTab(
                            uiState = uiState,
                            attendanceState = attendanceViewModel.uiState.collectAsState().value,
                            onNavigateToAiAssistant = onNavigateToAiAssistant, 
                            onNavigateToDigitalID = onNavigateToDigitalID
                        )"""
content = content.replace(old_call, new_call)

# Update definition
old_def = """fun StudentHomeTab(
    uiState: StudentDashboardUiState,
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToDigitalID: () -> Unit = {}
) {"""
new_def = """fun StudentHomeTab(
    uiState: StudentDashboardUiState,
    attendanceState: StudentAttendanceUiState? = null,
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToDigitalID: () -> Unit = {}
) {"""
content = content.replace(old_def, new_def)

# Update UI
old_attendance_ui = """                    CircularProgressIndicator(
                        progress = { 0.78f },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("78%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text("You are above", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("the required 75%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("View Details", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)"""

new_attendance_ui = """                    val progress = (attendanceState?.overallPercentage ?: 0f) / 100f
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
                    Text("View Details", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)"""
content = content.replace(old_attendance_ui, new_attendance_ui)

with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "w") as f:
    f.write(content)
