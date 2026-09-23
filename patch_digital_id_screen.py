with open('app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun DigitalIDScreen(viewModel: StudentDashboardViewModel, onBack: () -> Unit = {}) {', 'fun DigitalIDScreen(viewModel: DigitalIDViewModel, onBack: () -> Unit = {}) {')
content = content.replace('val uiState by viewModel.uiState.collectAsState()\\n    val user = uiState.user', 'val user by viewModel.user.collectAsState()')

with open('app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt', 'w') as f:
    f.write(content)
