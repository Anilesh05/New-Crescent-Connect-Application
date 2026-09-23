import re

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

target = """                onNavigateToCourseChat = { courseId, name, code -> navController.navigate(Route.CourseChat(courseId, name, code)) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )"""

replacement = """                onNavigateToCourseChat = { courseId, name, code -> navController.navigate(Route.CourseChat(courseId, name, code)) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                },
                sessionManager = sessionManager
            )"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
