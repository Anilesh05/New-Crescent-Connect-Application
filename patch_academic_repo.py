import re

with open("app/src/main/java/com/example/data/repository/AcademicRepositoryImpl.kt", "r") as f:
    content = f.read()

# Add import
content = content.replace("import com.example.domain.repository.AcademicRepository", "import com.example.domain.repository.AcademicRepository\nimport com.example.domain.repository.FileStorageService")

# Inject FileStorageService
content = content.replace(
    "class AcademicRepositoryImpl(private val dao: AcademicDao, private val context: android.content.Context) : AcademicRepository {",
    "class AcademicRepositoryImpl(private val dao: AcademicDao, private val context: android.content.Context, private val fileStorageService: FileStorageService? = null) : AcademicRepository {"
)

with open("app/src/main/java/com/example/data/repository/AcademicRepositoryImpl.kt", "w") as f:
    f.write(content)

