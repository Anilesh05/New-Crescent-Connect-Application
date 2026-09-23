with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

import re

# Add imports
imports = """import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.data.local.SessionManager
"""
content = content.replace("import com.example.ui.theme.CrescentConnectTheme", "import com.example.ui.theme.CrescentConnectTheme\n" + imports)

# Update setContent block
setContentReplacement = """        val sessionManager = SessionManager(this)

        setContent {
            val themePreference by sessionManager.themeFlow.collectAsState(initial = "system")
            val isDarkTheme = when (themePreference) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            CrescentConnectTheme(darkTheme = isDarkTheme) {
                CrescentConnectApp(modifier = Modifier.fillMaxSize(), context = this)
            }
        }"""
content = re.sub(r'setContent \{.*?\}', setContentReplacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
