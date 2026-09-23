with open("app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt", "r") as f:
    content = f.read()

import re

# Replace details rendering
old_details = """                    // Details
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        IDDetailRow("Register Number", user.registerNumber ?: "-")
                        IDDetailRow("Programme", user.programme ?: "-")
                        IDDetailRow("Department", user.department ?: "-")
                        IDDetailRow("Semester / Section", "${user.semester ?: "-"} / ${user.section ?: "-"}")
                        IDDetailRow("Academic Year", user.academicYear ?: "-")
                    }"""

new_details = """                    // Details
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (user.role == com.example.domain.model.Role.STUDENT || user.role == com.example.domain.model.Role.CR) {
                            IDDetailRow("Register Number", user.registerNumber ?: "-")
                            IDDetailRow("Programme", user.programme ?: "-")
                            IDDetailRow("Department", user.department ?: "-")
                            IDDetailRow("Semester / Section", "${user.semester ?: "-"} / ${user.section ?: "-"}")
                            IDDetailRow("Academic Year", user.academicYear ?: "-")
                        } else {
                            IDDetailRow("Staff ID", user.registerNumber ?: user.id)
                            IDDetailRow("Department", user.department ?: "-")
                            IDDetailRow("Designation", user.designation ?: "-")
                            IDDetailRow("Email", user.email)
                        }
                    }"""

content = content.replace(old_details, new_details)

with open("app/src/main/java/com/example/ui/dashboard/DigitalIDScreen.kt", "w") as f:
    f.write(content)
