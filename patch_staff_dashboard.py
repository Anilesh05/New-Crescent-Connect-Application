with open("app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt", "r") as f:
    content = f.read()

new_home_tab = """@Composable
fun StaffHomeTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = "Welcome, Mr. Aravind 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Assistant Professor - CSE",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Access Cards Grid
        Text("Quick Access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "My Classes", value = "4", icon = Icons.Filled.DateRange, iconColor = Color(0xFF2563EB), bgColor = Color(0xFFEFF6FF))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Students", value = "72", icon = Icons.Filled.People, iconColor = Color(0xFF16A34A), bgColor = Color(0xFFF0FDF4))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "Attendance", value = "Mark & View", icon = Icons.Filled.Checklist, iconColor = Color(0xFFDC2626), bgColor = Color(0xFFFEF2F2))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Marks", value = "Enter & View", icon = Icons.Filled.Edit, iconColor = Color(0xFFCA8A04), bgColor = Color(0xFFFEFCE8))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StaffActionCard(modifier = Modifier.weight(1f), title = "Study Materials", value = "Upload", icon = Icons.Filled.MenuBook, iconColor = Color(0xFF0D9488), bgColor = Color(0xFFF0FDFA))
                StaffActionCard(modifier = Modifier.weight(1f), title = "Announcements", value = "Create", icon = Icons.Filled.Campaign, iconColor = Color(0xFF7C3AED), bgColor = Color(0xFFF5F3FF))
            }
        }
        
        Text("Today's Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("DBMS - MCA II Year", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("10:00 AM - 11:40 AM | Block A - 302", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Pending", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Data Structures - B.Tech I Year", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("02:00 PM - 03:40 PM | Block B - 104", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}"""

# Find the old StaffHomeTab block and replace it
import re
pattern = re.compile(r"@Composable\nfun StaffHomeTab\(\) \{.*?\}\n\}\n", re.DOTALL)
content = pattern.sub(new_home_tab + "\n", content)

with open("app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt", "w") as f:
    f.write(content)
