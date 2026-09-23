with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

import_stmt = "import com.example.ui.verification.QrScannerScreen\\nimport com.example.ui.verification.ScanResultScreen\\n"
if "import com.example.ui.verification.QrScannerScreen" not in content:
    content = content.replace("import com.example.ui.dashboard.StaffDashboard", import_stmt + "import com.example.ui.dashboard.StaffDashboard")

screen_logic = """
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
if "composable<Route.ScanID>" not in content:
    content = content.replace("composable<Route.DigitalID> {", screen_logic + "\\n        composable<Route.DigitalID> {")

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
