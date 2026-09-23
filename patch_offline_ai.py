with open('app/src/main/java/com/example/data/repository/AiServiceImpl.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'return@withContext "AI OFFLINE / UNAVAILABLE\\n\\nAI functionality is disabled because the API key is not configured."',
    'return@withContext "AI OFFLINE / UNAVAILABLE\\n\\nAI functionality is disabled.\\n\\nRaw Data Context:\\n" + prompt.substringAfter("Context:")'
)

content = content.replace(
    '"AI OFFLINE / UNAVAILABLE\\n\\nError: ${e.message}"',
    '"AI OFFLINE / UNAVAILABLE\\n\\nError: ${e.message}\\n\\nRaw Data Context:\\n" + prompt.substringAfter("Context:")'
)

with open('app/src/main/java/com/example/data/repository/AiServiceImpl.kt', 'w') as f:
    f.write(content)
