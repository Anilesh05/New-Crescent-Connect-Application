with open("app/src/main/java/com/example/ui/dashboard/ProfileTab.kt", "r") as f:
    content = f.read()

import re

# Change parameter from `uiState: StudentDashboardUiState` to `user: com.example.domain.model.User?`
content = content.replace("uiState: StudentDashboardUiState", "user: com.example.domain.model.User?")

# Replace uiState.user?. with user?.
content = content.replace("uiState.user?", "user?")

# Add onThemeToggle callback
content = content.replace("onLogout: () -> Unit = {}", "onLogout: () -> Unit = {}, onThemeToggle: () -> Unit = {}")

# Make text dynamic based on role
content = content.replace(
    'Text("${user?.programme ?: ""} • ${user?.semester ?: ""} • Section ${user?.section ?: ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)',
    '''if (user?.role == com.example.domain.model.Role.STAFF || user?.role == com.example.domain.model.Role.CLASS_ADVISER || user?.role == com.example.domain.model.Role.ADMIN) {
                Text("${user.department ?: "Department"} • ${user.designation ?: "Staff"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text("${user?.programme ?: ""} • ${user?.semester ?: ""} • Section ${user?.section ?: ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }'''
)

# Add Theme switch and conditional rows
menu_replacement = """
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMenuRow(Icons.Filled.Badge, "Digital ID", Color(0xFF0D9488), onClick = onNavigateToDigitalID)
            if (user?.role == com.example.domain.model.Role.STUDENT || user?.role == com.example.domain.model.Role.CR) {
                ProfileMenuRow(Icons.Filled.Description, "Resume", Color(0xFFCA8A04))
                ProfileMenuRow(Icons.Filled.Stars, "Skills & Certifications", Color(0xFFEA580C))
                ProfileMenuRow(Icons.Filled.Folder, "Projects", Color(0xFF2563EB))
                ProfileMenuRow(Icons.Filled.School, "Academic Profile", Color(0xFF7C3AED))
            }
            ProfileMenuRow(Icons.Filled.Palette, "Toggle Theme", Color(0xFF8B5CF6), onClick = onThemeToggle)
            ProfileMenuRow(Icons.Filled.Settings, "Settings", Color(0xFF4B5563))
            ProfileMenuRow(Icons.Filled.Logout, "Logout", MaterialTheme.colorScheme.error, onClick = onLogout)
        }
"""
content = re.sub(r'Column\(verticalArrangement = Arrangement.spacedBy\(12.dp\)\) \{.*?\n        \}', menu_replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/dashboard/ProfileTab.kt", "w") as f:
    f.write(content)
