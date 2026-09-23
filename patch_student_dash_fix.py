with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "r") as f:
    content = f.read()

content = content.replace("StudentHomeTab(\n                            user = uiState.user,", "StudentHomeTab(\n                            uiState = uiState,")
content = content.replace("StudentMessagesTab(user = uiState.user,", "StudentMessagesTab(uiState = uiState,")

with open("app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt", "w") as f:
    f.write(content)
