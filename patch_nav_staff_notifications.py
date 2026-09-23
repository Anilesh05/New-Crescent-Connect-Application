with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onNavigateToCourseChat = { courseId, staffId, staffName ->',
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onNavigateToNotifications = { navController.navigate(Route.Notifications) },\n                onNavigateToCourseChat = { courseId, staffId, staffName ->'
)

content = content.replace(
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onLogout = {',
    'onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onNavigateToNotifications = { navController.navigate(Route.Notifications) },\n                onLogout = {'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
