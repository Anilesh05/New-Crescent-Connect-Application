import re

with open("app/src/main/java/com/example/ui/dashboard/AdminDashboard.kt", "r", encoding='utf-8') as f:
    content = f.read()

# Let's just fix the welcome message using string replacement
old_welcome = """        Column {
            Text(
                text = "Welcome, Admin 👋",
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
        
new_welcome = """        Column {
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
        }"""
        
content = content.replace(old_welcome, new_welcome)

with open("app/src/main/java/com/example/ui/dashboard/AdminDashboard.kt", "w", encoding='utf-8') as f:
    f.write(content)
