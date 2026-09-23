with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

import_stmt = "import com.example.ui.verification.QrScannerScreen\nimport com.example.ui.verification.ScanResultScreen\nimport com.example.ui.dashboard.DigitalIDViewModel\n"

if "import com.example.ui.verification.QrScannerScreen" not in content:
    content = content.replace("import com.example.ui.dashboard.StaffDashboard", import_stmt + "import com.example.ui.dashboard.StaffDashboard")

content = content.replace(
    'val viewModel = remember { StudentDashboardViewModel(userRepository, courseRepository, sessionManager) }\n            DigitalIDScreen(viewModel = viewModel, onBack = { navController.popBackStack() })',
    'val viewModel = remember { DigitalIDViewModel(userRepository, sessionManager) }\n            DigitalIDScreen(viewModel = viewModel, onBack = { navController.popBackStack() })'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
