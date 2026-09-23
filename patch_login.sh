sed -i 's/var passwordVisible by remember { mutableStateOf(false) }/var passwordVisible by remember { mutableStateOf(false) }\n    var showDemoDialog by remember { mutableStateOf(false) }/' app/src/main/java/com/example/ui/auth/LoginScreen.kt
sed -i '/\/\/ Footer/i\
            TextButton(onClick = { showDemoDialog = true }) {\n                Text("Demo Accounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\n            }\n            Spacer(modifier = Modifier.height(16.dp))' app/src/main/java/com/example/ui/auth/LoginScreen.kt

cat << 'INNER_EOF' >> app/src/main/java/com/example/ui/auth/LoginScreen.kt

@Composable
fun DemoAccountsDialog(
    onDismiss: () -> Unit,
    onSelectDemo: (String, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Demo Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val demos = listOf(
                    "Student" to ("student@crescentconnect.demo" to "Student@123"),
                    "Staff" to ("staff@crescentconnect.demo" to "Staff@123"),
                    "Class Adviser" to ("advisor@crescentconnect.demo" to "Advisor@123"),
                    "CR" to ("cr@crescentconnect.demo" to "CR@123"),
                    "Admin" to ("admin@crescentconnect.demo" to "Admin@123")
                )
                demos.forEach { (role, creds) ->
                    Card(
                        onClick = { onSelectDemo(creds.first, creds.second) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(role, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(creds.first, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
INNER_EOF

sed -i '/if (showDemoDialog) {/d' app/src/main/java/com/example/ui/auth/LoginScreen.kt
sed -i 's/    Scaffold(/    if (showDemoDialog) {\n        DemoAccountsDialog(onDismiss = { showDemoDialog = false }) { selEmail, selPwd ->\n            email = selEmail\n            password = selPwd\n            showDemoDialog = false\n        }\n    }\n\n    Scaffold(/' app/src/main/java/com/example/ui/auth/LoginScreen.kt

