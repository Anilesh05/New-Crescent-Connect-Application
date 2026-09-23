with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val viewModel = remember { StudentDashboardViewModel(userRepository, courseRepository, sessionManager) }\\n            DigitalIDScreen(viewModel = viewModel',
    'val viewModel = remember { com.example.ui.dashboard.DigitalIDViewModel(userRepository, sessionManager) }\\n            DigitalIDScreen(viewModel = viewModel'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
