with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("import java.util.UUID\npackage com.example.ui.dashboard\n", "package com.example.ui.dashboard\nimport java.util.UUID\n")

with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'w') as f:
    f.write(content)
