with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

# Fix ClassAdviserDashboard
content = content.replace('''                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                },
                sessionManager = sessionManager''', '''                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }''')

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)
