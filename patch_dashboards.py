with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "r") as f:
    content = f.read()

import re

# Add imports
content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.runtime.rememberCoroutineScope\nimport kotlinx.coroutines.launch\nimport com.example.ui.components.ThemeSelectionDialog\nimport com.example.data.local.SessionManager")

# Add sessionManager parameter
content = content.replace(
    "onLogout: () -> Unit = {}",
    "onLogout: () -> Unit = {},\n    sessionManager: SessionManager"
)

# Add dialog state
state_code = """    var showThemeDialog by remember { mutableStateOf(false) }
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

content = content.replace("Scaffold(", state_code + "\n    Scaffold(")

# Find ProfileTab invocation and add onThemeToggle
content = content.replace(
    "onLogout = {\n                                viewModel.logout()\n                                onLogout()\n                            }",
    "onLogout = {\n                                viewModel.logout()\n                                onLogout()\n                            },\n                            onThemeToggle = { showThemeDialog = true }"
)

# Use uiState.user? directly in ProfileTab
content = content.replace("uiState = uiState", "user = uiState.user")

with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "w") as f:
    f.write(content)
