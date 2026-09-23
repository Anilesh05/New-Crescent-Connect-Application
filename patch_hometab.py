with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'r') as f:
    content = f.read()

# I want to add "Recent Announcements" above "Enrolled Courses" in StudentHomeTab
announcements_ui = """
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
        
        // Upcoming"""

content = content.replace('// Upcoming', announcements_ui)

with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'w') as f:
    f.write(content)
