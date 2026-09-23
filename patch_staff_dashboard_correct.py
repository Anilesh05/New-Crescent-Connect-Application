with open('app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt', 'r') as f:
    content = f.read()

import_stmt = "import androidx.compose.material.icons.filled.QrCodeScanner\nimport androidx.compose.material3.IconButton\n"

content = content.replace("import androidx.compose.material.icons.filled.Search", import_stmt + "import androidx.compose.material.icons.filled.Search")

content = content.replace(
    'fun StaffDashboard(modifier: Modifier = Modifier, attendanceViewModel: StaffAttendanceViewModel) {',
    'fun StaffDashboard(modifier: Modifier = Modifier, attendanceViewModel: StaffAttendanceViewModel, onNavigateToScanID: () -> Unit = {}) {'
)

scan_icon = """            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = "DK",
                actions = {
                    IconButton(onClick = onNavigateToScanID) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan ID", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )"""

content = content.replace("""            CrescentHeader(
                title = "CrescentConnect",
                subtitle = "BSARCIST",
                avatarText = "DK"
            )""", scan_icon)

with open('app/src/main/java/com/example/ui/dashboard/StaffDashboard.kt', 'w') as f:
    f.write(content)
