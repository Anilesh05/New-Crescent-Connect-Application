import re

# 1. DigitalIDScreen.kt
with open('app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('val user by viewModel.user.collectAsState()\n    Scaffold(', 'val userState by viewModel.user.collectAsState()\n    val user = userState\n    Scaffold(')

with open('app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt', 'w') as f:
    f.write(content)


# 2. AppNavigation.kt
with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

import_stmt = "import com.example.ui.verification.QrScannerScreen\nimport com.example.ui.verification.ScanResultScreen\nimport com.example.ui.dashboard.DigitalIDViewModel\n"
if "import com.example.ui.verification.QrScannerScreen" not in content:
    content = content.replace('package com.example.ui.navigation\n', 'package com.example.ui.navigation\n\n' + import_stmt)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)


# 3. QrScannerScreen.kt
with open('app/src/main/java/com/example/ui/verification/QrScannerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('@kotlin.OptIn(androidx.camera.core.ExperimentalGetImage::class)', '@androidx.camera.core.ExperimentalGetImage')

with open('app/src/main/java/com/example/ui/verification/QrScannerScreen.kt', 'w') as f:
    f.write(content)
