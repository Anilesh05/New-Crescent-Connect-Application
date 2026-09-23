with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'r') as f:
    content = f.read()

import_statement = """import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
"""

content = content.replace("import androidx.compose.runtime.LaunchedEffect", import_statement + "import androidx.compose.runtime.LaunchedEffect")

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'w') as f:
    f.write(content)
