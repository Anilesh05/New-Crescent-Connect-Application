with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'r') as f:
    content = f.read()

# Update signature
content = content.replace(
    'onNavigateToCourseChat: () -> Unit = {}',
    'onNavigateToCourseChat: (String, String, String) -> Unit = {_,_,_ ->}'
)

# Update onClick
content = content.replace(
    'modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToCourseChat),',
    'modifier = Modifier.fillMaxWidth().clickable(onClick = { onNavigateToCourseChat(course.id, course.subjectName, course.subjectCode) }),'
)

with open('app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt', 'w') as f:
    f.write(content)
