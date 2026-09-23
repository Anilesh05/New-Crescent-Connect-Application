import re
with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("                onLogout = {\n                    navController.navigate(Route.Login) { popUpTo(0) }\n                }\n            )", "                onLogout = {\n                    navController.navigate(Route.Login) { popUpTo(0) }\n                },\n                sessionManager = sessionManager\n            )")

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
