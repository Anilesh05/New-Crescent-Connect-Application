with open('app/src/main/java/com/example/data/local/entity/Entities.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'val isActive: Boolean,',
    'val isActive: Boolean,\n    val status: String = "ACTIVE",\n    val idVersion: Int = 1,\n    val updatedAt: Long? = null,'
)

with open('app/src/main/java/com/example/data/local/entity/Entities.kt', 'w') as f:
    f.write(content)
