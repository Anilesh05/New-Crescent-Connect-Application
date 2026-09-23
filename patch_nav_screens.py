with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

import_stmt = """import com.example.ui.verification.QrScannerScreen
import com.example.ui.verification.ScanResultScreen"""

content = content.replace("import com.example.ui.dashboard.StaffDashboard", import_stmt + "\nimport com.example.ui.dashboard.StaffDashboard")

screen_logic = """
        composable<Route.DigitalID> {
            val parentEntry = remember(it) { navController.getBackStackEntry(Route.StudentDashboard) }
            val viewModel: StudentDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry, factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return StudentDashboardViewModel(authRepository, userRepository) as T
                }
            })
            DigitalIDScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        
        composable<Route.ScanID> {
            QrScannerScreen(
                onBack = { navController.popBackStack() },
                onQrCodeDetected = { payload ->
                    navController.popBackStack()
                    navController.navigate(Route.ScanResult(payload))
                }
            )
        }
        
        composable<Route.ScanResult> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.ScanResult>()
            ScanResultScreen(
                payload = route.qrPayload,
                userRepository = userRepository,
                onBack = { navController.popBackStack() }
            )
        }
"""

content = content.replace("""        composable<Route.DigitalID> {
            val parentEntry = remember(it) { navController.getBackStackEntry(Route.StudentDashboard) }
            val viewModel: StudentDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry, factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return StudentDashboardViewModel(authRepository, userRepository) as T
                }
            })
            DigitalIDScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }""", screen_logic)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
