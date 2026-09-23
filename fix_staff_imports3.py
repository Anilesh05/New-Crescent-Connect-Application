with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'r') as f:
    content = f.read()

# Replace all occurrences of import java.util.UUID to just one after the package declaration
content = content.replace("import java.util.UUID\\n", "")
content = content.replace("import java.util.UUID", "")

content = content.replace("package com.example.ui.dashboard", "package com.example.ui.dashboard\\nimport java.util.UUID")

with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'w') as f:
    f.write(content)
