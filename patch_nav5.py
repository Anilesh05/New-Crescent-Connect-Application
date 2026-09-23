import re

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

target = """                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }
        
        composable<Route.ScanID>"""

replacement = """                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                },
                sessionManager = sessionManager
            )
        }
        
        composable<Route.ScanID>"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
