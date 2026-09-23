with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

content = content.replace('  // implementation(libs.androidx.camera.camera2)', '  implementation(libs.androidx.camera.camera2)')
content = content.replace('  // implementation(libs.androidx.camera.core)', '  implementation(libs.androidx.camera.core)')
content = content.replace('  // implementation(libs.androidx.camera.lifecycle)', '  implementation(libs.androidx.camera.lifecycle)')
content = content.replace('  // implementation(libs.androidx.camera.view)', '  implementation(libs.androidx.camera.view)')

deps_to_add = """
  implementation(libs.zxing.core)
  implementation(libs.mlkit.barcode)
"""
if 'implementation(libs.zxing.core)' not in content:
    content = content.replace('  implementation(libs.googleid)', '  implementation(libs.googleid)\n' + deps_to_add)

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
