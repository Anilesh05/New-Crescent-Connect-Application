with open('app/src/main/java/com/example/data/local/DatabaseSeeder.kt', 'r') as f:
    content = f.read()
import re
content = re.sub(r'UserEntity\(([^)]*)\)', lambda m: m.group(0).replace('true)', 'true, "ACTIVE", 1, System.currentTimeMillis())'), content)
with open('app/src/main/java/com/example/data/local/DatabaseSeeder.kt', 'w') as f:
    f.write(content)
