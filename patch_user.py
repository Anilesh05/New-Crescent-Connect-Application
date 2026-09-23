with open('app/src/main/java/com/example/domain/model/User.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val isActive: Boolean = true,',
    'val isActive: Boolean = true,\n    val status: String = "ACTIVE",\n    val idVersion: Int = 1,\n    val updatedAt: Long? = null,'
)

with open('app/src/main/java/com/example/domain/model/User.kt', 'w') as f:
    f.write(content)
