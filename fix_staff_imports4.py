with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("package com.example.ui.dashboard\\nimport java.util.UUID", "package com.example.ui.dashboard\nimport java.util.UUID\n")
content = content.replace("import java.util.UUID\\npackage com.example.ui.dashboard", "package com.example.ui.dashboard\nimport java.util.UUID\n")

# Let's ensure it's at the very top
lines = content.split('\n')
clean_lines = [line for line in lines if not line.startswith('package') and not line.startswith('import java.util.UUID')]

final_content = "package com.example.ui.dashboard\nimport java.util.UUID\n" + "\n".join(clean_lines)

with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'w') as f:
    f.write(final_content)
