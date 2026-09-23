with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'StaffDashboard(\n                viewModel = viewModel,',
    'StaffDashboard(\n                viewModel = viewModel,\n                onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
