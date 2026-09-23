with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

if "data object Notifications : Route()" not in content:
    content = content.replace(
        'data object AiAssistant : Route()',
        'data object AiAssistant : Route()\n    @Serializable data object Notifications : Route()'
    )

if "import com.example.ui.dashboard.NotificationsViewModel" not in content:
    content = content.replace(
        'import com.example.ui.dashboard.DigitalIDViewModel',
        'import com.example.ui.dashboard.DigitalIDViewModel\nimport com.example.ui.dashboard.NotificationsViewModel\nimport com.example.ui.dashboard.NotificationsScreen'
    )

if "composable<Route.Notifications>" not in content:
    route = '''        composable<Route.Notifications> {
            val viewModel = remember { NotificationsViewModel(sessionManager, academicRepository) }
            NotificationsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }'''
    content = content.replace(
        '        composable<Route.AiAssistant> {',
        route + '\n        composable<Route.AiAssistant> {'
    )

# update StudentDashboard
content = content.replace(
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },',
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onNavigateToNotifications = { navController.navigate(Route.Notifications) },'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
