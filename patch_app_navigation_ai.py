with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

import_stmt = "import com.example.ui.dashboard.AiAssistantViewModel\nimport com.example.data.repository.AiServiceImpl\n"
if "import com.example.ui.dashboard.AiAssistantViewModel" not in content:
    content = content.replace("import com.example.ui.dashboard.DigitalIDViewModel", import_stmt + "import com.example.ui.dashboard.DigitalIDViewModel")

logic = '''        composable<Route.AiAssistant> {
            val aiService = remember { AiServiceImpl("YOUR_API_KEY") }
            val viewModel = remember { AiAssistantViewModel(sessionManager, userRepository, courseRepository, attendanceRepository, aiService) }
            AiAssistantScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }'''

content = content.replace(
    '        composable<Route.AiAssistant> {\n            AiAssistantScreen(onBack = { navController.popBackStack() })\n        }',
    logic
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
