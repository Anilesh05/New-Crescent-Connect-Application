import re

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r", encoding='utf-8') as f:
    content = f.read()

# Replace any occurrence of two sessionManager = sessionManager
content = re.sub(r',\s*sessionManager = sessionManager\s*,\s*sessionManager = sessionManager', r',\n                sessionManager = sessionManager', content)

# Check for trailing sessionManager after onLogout
content = re.sub(r'onLogout = \{([^}]*)\}\s*,\s*sessionManager = sessionManager\s*,\s*sessionManager = sessionManager\s*\)', r'onLogout = {\1},\n                sessionManager = sessionManager\n            )', content)

# Wait, the duplication is like this:
'''
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                },
                sessionManager = sessionManager,
                sessionManager = sessionManager
'''
# let's just do:
content = content.replace("sessionManager = sessionManager,\n                sessionManager = sessionManager", "sessionManager = sessionManager")

# For AdminDashboard:
content = content.replace("sessionManager = sessionManager,\n                onNavigateToDigitalID", "onNavigateToDigitalID")
content = content.replace("sessionManager = sessionManager,\n                onLogout", "onLogout")

# Let's just remove ALL `sessionManager = sessionManager` and put them back correctly.
content = content.replace("sessionManager = sessionManager,", "")
content = content.replace("sessionManager = sessionManager", "")
# Some might leave empty commas, let's just be careful.

