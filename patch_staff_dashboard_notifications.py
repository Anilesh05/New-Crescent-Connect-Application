with open('app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'onNavigateToAiAssistant: () -> Unit = {},',
    'onNavigateToAiAssistant: () -> Unit = {},\n    onNavigateToNotifications: () -> Unit = {},'
)

header = '''        topBar = {
            CrescentHeader(
                title = "CrescentConnect Staff",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "ST",
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
                title = "CrescentConnect Staff",
                subtitle = "BSARCIST",
                avatarText = uiState.user?.name?.take(2)?.uppercase() ?: "ST"
            )
        },''',
    header
)

with open('app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'fun ClassAdviserDashboard(onNavigateToAiAssistant: () -> Unit = {}, onLogout: () -> Unit) {',
    'fun ClassAdviserDashboard(onNavigateToAiAssistant: () -> Unit = {}, onNavigateToNotifications: () -> Unit = {}, onLogout: () -> Unit) {'
)

header = '''    Scaffold(
        topBar = {
            CrescentHeader(
                title = "Class Adviser",
                subtitle = "BSARCIST",
                avatarText = "AD",
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->'''

content = content.replace(
    '''    Scaffold(
        topBar = {
            CrescentHeader(
                title = "Class Adviser",
                subtitle = "BSARCIST",
                avatarText = "AD"
            )
        }
    ) { paddingValues ->''',
    header
)

with open('app/src/main/java/com/example/ui/dashboard/ClassAdviserDashboard.kt', 'w') as f:
    f.write(content)
