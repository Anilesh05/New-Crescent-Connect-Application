with open('app/src/main/java/com/example/data/repository/RepositoryImpls.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'isActive = isActive,',
    'isActive = isActive,\n    status = status,\n    idVersion = idVersion,\n    updatedAt = updatedAt,'
)

content = content.replace(
    'isActive = user.isActive,',
    'isActive = user.isActive,\n            status = user.status,\n            idVersion = user.idVersion,\n            updatedAt = user.updatedAt,'
)

with open('app/src/main/java/com/example/data/repository/RepositoryImpls.kt', 'w') as f:
    f.write(content)
