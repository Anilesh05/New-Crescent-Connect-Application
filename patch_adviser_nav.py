with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'ClassAdviserDashboard(onLogout = {',
    'ClassAdviserDashboard(\n                onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },\n                onLogout = {'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
