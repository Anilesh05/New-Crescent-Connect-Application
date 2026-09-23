with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'import androidx.compose.foundation.verticalScroll',
    'import androidx.compose.foundation.verticalScroll\nimport androidx.compose.foundation.clickable'
)

with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'w') as f:
    f.write(content)
