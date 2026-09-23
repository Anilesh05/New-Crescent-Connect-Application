with open('app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt', 'r') as f:
    content = f.read()

if 'onNavigateToAiAssistant' not in content:
    content = content.replace(
        'fun ClassAdviserDashboard(onLogout: () -> Unit) {',
        'fun ClassAdviserDashboard(onNavigateToAiAssistant: () -> Unit = {}, onLogout: () -> Unit) {'
    )
    content = content.replace(
        'Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {\n            Text("Logout")\n        }',
        'Button(onClick = onNavigateToAiAssistant, modifier = Modifier.fillMaxWidth()) {\n            Text("Open AI Assistant")\n        }\n        Spacer(modifier = Modifier.height(16.dp))\n        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {\n            Text("Logout")\n        }'
    )

with open('app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt', 'w') as f:
    f.write(content)
