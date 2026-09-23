with open('app/src/main/java/com/example/domain/model/User.kt', 'r') as f:
    content = f.read()

new_user = """data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val registerNumber: String? = null,
    val programme: String? = null,
    val department: String? = null,
    val semester: String? = null,
    val section: String? = null,
    val designation: String? = null,
    val academicYear: String? = null,
    val profileImageUri: String? = null,
    val isActive: Boolean = true,
    val skills: String? = null,
    val certifications: String? = null,
    val projects: String? = null,
    val internships: String? = null,
    val achievements: String? = null
)"""

import re
content = re.sub(r'data class User\(.*?isActive: Boolean = true\n\)', new_user, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/domain/model/User.kt', 'w') as f:
    f.write(content)
