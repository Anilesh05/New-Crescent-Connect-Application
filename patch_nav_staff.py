with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'StaffDashboard(attendanceViewModel = staffAttendanceViewModel)',
    'StaffDashboard(attendanceViewModel = staffAttendanceViewModel, onNavigateToScanID = { navController.navigate(Route.ScanID) })'
)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
