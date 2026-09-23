with open("app/src/main/java/com/example/ui/dashboard/AcademicViewModel.kt", "r") as f:
    content = f.read()

if "FileStorageService" not in content:
    content = content.replace(
        "import com.example.domain.repository.AcademicRepository",
        "import com.example.domain.repository.AcademicRepository\nimport com.example.domain.repository.FileStorageService"
    )
    content = content.replace(
        "class AcademicViewModel(",
        "class AcademicViewModel(\n    private val fileStorageService: FileStorageService,"
    )

with open("app/src/main/java/com/example/ui/dashboard/AcademicViewModel.kt", "w") as f:
    f.write(content)
