with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

import re

# Add UserDashboardViewModel and StaffDashboardViewModel to imports
content = content.replace("import com.example.ui.dashboard.*", "import com.example.ui.dashboard.*\nimport com.example.ui.dashboard.StaffDashboardViewModel\nimport com.example.ui.dashboard.UserDashboardViewModel")

# Patch ClassAdviserDashboard
adviser_patch = """        composable<Route.ClassAdviserDashboard> {
            val viewModel = remember { UserDashboardViewModel(userRepository, sessionManager) }
            ClassAdviserDashboard(
                viewModel = viewModel,
                sessionManager = sessionManager,
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }"""
content = re.sub(r'        composable<Route\.ClassAdviserDashboard> \{\n            ClassAdviserDashboard\(\)\n        \}', adviser_patch, content, flags=re.DOTALL)

# Patch AdminDashboard
admin_patch = """        composable<Route.AdminDashboard> {
            val viewModel = remember { UserDashboardViewModel(userRepository, sessionManager) }
            AdminDashboard(
                viewModel = viewModel,
                sessionManager = sessionManager,
                onNavigateToDigitalID = { navController.navigate(Route.DigitalID) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }"""
content = re.sub(r'        composable<Route\.AdminDashboard> \{\n            AdminDashboard\(\)\n        \}', admin_patch, content, flags=re.DOTALL)

# Patch StaffDashboard
staff_patch = """        composable<Route.StaffDashboard> {
            val staffAttendanceViewModel = remember { StaffAttendanceViewModel(attendanceRepository, courseRepository, userRepository, sessionManager) }
            val viewModel = remember { StaffDashboardViewModel(userRepository, courseRepository, sessionManager) }
            StaffDashboard(
                viewModel = viewModel,
                attendanceViewModel = staffAttendanceViewModel, 
                sessionManager = sessionManager,
                onNavigateToScanID = { navController.navigate(Route.ScanID) },
                onNavigateToDigitalID = { navController.navigate(Route.DigitalID) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }"""
content = re.sub(r'        composable<Route\.StaffDashboard> \{\n            val staffAttendanceViewModel = remember \{ StaffAttendanceViewModel\(attendanceRepository, courseRepository, userRepository, sessionManager\) \}\n            StaffDashboard\(attendanceViewModel = staffAttendanceViewModel, onNavigateToScanID = \{ navController\.navigate\(Route\.ScanID\) \}\)\n        \}', staff_patch, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
