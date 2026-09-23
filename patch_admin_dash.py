import re

with open("app/src/main/java/com/example/ui/dashboard/AdminDashboard.kt", "r", encoding='utf-8') as f:
    content = f.read()

imports = """import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager
"""
content = content.replace("import androidx.compose.ui.Modifier", imports)

signature = """@Composable
fun AdminDashboard(
    modifier: Modifier = Modifier, 
    viewModel: UserDashboardViewModel,
    sessionManager: SessionManager,
    onNavigateToDigitalID: () -> Unit = {},
    onLogout: () -> Unit = {}
) {"""
content = re.sub(r'@Composable\nfun AdminDashboard\(.*?\) \{', signature, content, flags=re.DOTALL)

body_start = """    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Students", "Attendance", "Reports", "Profile")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.People, Icons.Filled.Checklist, Icons.Filled.BarChart, Icons.Filled.Person)
    
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
content = re.sub(r'    var selectedItem by remember \{ mutableIntStateOf\(0\) \}.*?val icons = listOf\(.*?\)', body_start, content, flags=re.DOTALL)

content = content.replace("0 -> AdminHomeTab()", "0 -> AdminHomeTab(uiState)")

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

home_tab_sig = """@Composable
fun AdminHomeTab(uiState: UserDashboardUiState) {"""
content = content.replace("@Composable\nfun AdminHomeTab() {", home_tab_sig)

welcome_msg = """        Column {
            Text(
                text = "Welcome, ${uiState.user?.name ?: "Admin"} \\uD83D\\uDC4B",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "System Administration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }"""
content = re.sub(r'        Column \{\n            Text\(\n                text = "Welcome, Admin.*?\n            \)\n        \}', welcome_msg, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/dashboard/AdminDashboard.kt", "w", encoding='utf-8') as f:
    f.write(content)
