with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'onNavigateToAiAssistant: () -> Unit = {},',
    'onNavigateToAiAssistant: () -> Unit = {},\n    onNavigateToNotifications: () -> Unit = {},'
)

header = '''        topBar = {
            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "AN",
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },'''

content = content.replace(
    '''        topBar = {
            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "AN"
            )
        },''',
    header
)

with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'w') as f:
    f.write(content)
