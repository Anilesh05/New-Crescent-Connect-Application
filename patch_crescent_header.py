with open('app/src/main/java/com/example/ui/components/CrescentComponents.kt', 'r') as f:
    content = f.read()

old_box = '''            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(avatarText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }'''
new_box = '''            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                actions()
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(avatarText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }'''
content = content.replace(old_box, new_box)

with open('app/src/main/java/com/example/ui/components/CrescentComponents.kt', 'w') as f:
    f.write(content)
