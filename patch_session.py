with open("app/src/main/java/com/example/data/local/SessionManager.kt", "r") as f:
    content = f.read()

import re

# Add THEME_PREF_KEY
theme_code = """
    private val THEME_PREF_KEY = stringPreferencesKey("theme_pref")

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_PREF_KEY] ?: "system"
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREF_KEY] = theme
        }
    }
"""

content = content.replace("suspend fun saveUserId(userId: String) {", theme_code + "\n    suspend fun saveUserId(userId: String) {")

with open("app/src/main/java/com/example/data/local/SessionManager.kt", "w") as f:
    f.write(content)
