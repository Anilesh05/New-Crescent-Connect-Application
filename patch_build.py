with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace('  implementation(libs.googleid)\\n', '  implementation(libs.googleid)\\n  implementation(libs.zxing.core)\\n  implementation(libs.mlkit.barcode)\\n')

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
