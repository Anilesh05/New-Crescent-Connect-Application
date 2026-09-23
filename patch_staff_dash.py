with open("app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt", "r") as f:
    content = f.read()

import re

imports = """import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager
"""
content = content.replace("import androidx.compose.ui.Modifier", imports)

signature = """@Composable
fun StaffDashboard(
    modifier: Modifier = Modifier, 
    viewModel: StaffDashboardViewModel,
    attendanceViewModel: StaffAttendanceViewModel, 
    sessionManager: SessionManager,
    onNavigateToScanID: () -> Unit = {},
    onNavigateToDigitalID: () -> Unit = {},
    onLogout: () -> Unit = {}
) {"""
content = re.sub(r'@Composable\nfun StaffDashboard\(.*?\) \{', signature, content, flags=re.DOTALL)

body_start = """    var selectedItem by remember { mutableIntStateOf(0) }
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
"""
content = content.replace("    var selectedItem by remember { mutableIntStateOf(0) }", body_start)

# Pass user to StaffHomeTab
content = content.replace("0 -> StaffHomeTab()", "0 -> StaffHomeTab(uiState)")

# Add ProfileTab
profile_tab = """4 -> ProfileTab(
                    user = uiState.user,
                    onNavigateToDigitalID = onNavigateToDigitalID,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                    onThemeToggle = { showThemeDialog = true }
                )"""
content = content.replace("else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(items[selectedItem]) }", profile_tab)

# Update StaffHomeTab signature
home_tab_sig = """@Composable
fun StaffHomeTab(uiState: StaffDashboardUiState) {"""
content = content.replace("@Composable\nfun StaffHomeTab() {", home_tab_sig)

# Update Welcome message in StaffHomeTab
welcome_msg = """        Column {
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
        }"""
content = re.sub(r'        Column \{\n            Text\(\n                text = "Welcome, Mr\. Aravind 👋",.*?\}\n', welcome_msg + "\n", content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt", "w") as f:
    f.write(content)
