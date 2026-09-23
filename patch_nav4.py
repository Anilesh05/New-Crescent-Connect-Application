import re

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

content = re.sub(
    r'                onLogout = \{\n                    navController\.navigate\(Route\.Login\) \{ popUpTo\(0\) \}\n                \}\n            \)\n        \}\n                \n        composable<Route\.ScanID>',
    r'                onLogout = {\n                    navController.navigate(Route.Login) { popUpTo(0) }\n                },\n                sessionManager = sessionManager\n            )\n        }\n                \n        composable<Route.ScanID>',
    content
)

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
