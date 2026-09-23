import re

with open("app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt", "r", encoding='utf-8') as f:
    content = f.read()

imports = """import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.ThemeSelectionDialog
import com.example.data.local.SessionManager
"""
content = content.replace("import androidx.compose.ui.Modifier", imports)

signature = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassAdviserDashboard(
    modifier: Modifier = Modifier, 
    viewModel: UserDashboardViewModel,
    sessionManager: SessionManager,
    onLogout: () -> Unit = {}
) {"""
content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun ClassAdviserDashboard\(.*?\) \{', signature, content, flags=re.DOTALL)

body_start = """    val uiState by viewModel.uiState.collectAsState()
    
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
                title = "Class Adviser Dashboard",
                subtitle = "MCA - II Year, Section A",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "AD",
                actions = {
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(Icons.Filled.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
"""
content = re.sub(r'    Scaffold\(\n        modifier = modifier,\n        topBar = \{\n            CrescentHeader\(\n                title = "Class Adviser Dashboard",\n                subtitle = "MCA - II Year, Section A",\n                avatarText = "AD"\n            \)\n        \}', body_start, content, flags=re.DOTALL)

old_welcome = """                Text(
                    text = "Welcome, Dr. Adviser 👋","""

new_welcome = """                Text(
                    text = "Welcome, ${uiState.user?.name ?: "Dr. Adviser"} 👋","""

content = content.replace(old_welcome, new_welcome)

with open("app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt", "w", encoding='utf-8') as f:
    f.write(content)
