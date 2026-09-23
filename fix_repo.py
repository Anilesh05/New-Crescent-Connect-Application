with open('app/src/main/java/com/example/domain/repository/Repositories.kt', 'r') as f:
    content = f.read()

import_statement = """import com.example.domain.model.*
"""

if "import com.example.domain.model.*" not in content:
    content = content.replace('import com.example.domain.model.CRPermission', 'import com.example.domain.model.CRPermission\n' + import_statement)

with open('app/src/main/java/com/example/domain/repository/Repositories.kt', 'w') as f:
    f.write(content)
