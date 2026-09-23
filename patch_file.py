import re

with open("app/src/main/java/com/example/ui/dashboard/AcademicViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("private val fileStorageService: FileStorageService,", "")

with open("app/src/main/java/com/example/ui/dashboard/AcademicViewModel.kt", "w") as f:
    f.write(content)
