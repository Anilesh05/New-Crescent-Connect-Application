with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace('import androidx.navigation.toRoute\npackage com.example.ui.navigation\n', 'package com.example.ui.navigation\nimport androidx.navigation.toRoute\n')

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
