with open('app/src/main/java/com/example/data/repository/RepositoryImpls.kt', 'r') as f:
    content = f.read()

import re

new_mapping = """fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = Role.valueOf(role),
    registerNumber = registerNumber,
    programme = programme,
    department = department,
    semester = semester,
    section = section,
    designation = designation,
    academicYear = academicYear,
    profileImageUri = profileImageUri,
    isActive = isActive,
    skills = skills,
    certifications = certifications,
    projects = projects,
    internships = internships,
    achievements = achievements
)"""

content = re.sub(r'fun UserEntity\.toDomain\(\) = User\(.*?isActive = isActive\n\)', new_mapping, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/data/repository/RepositoryImpls.kt', 'w') as f:
    f.write(content)
