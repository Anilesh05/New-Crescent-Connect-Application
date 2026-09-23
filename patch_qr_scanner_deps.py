with open('app/build.gradle.kts', 'r') as f:
    content = f.read()

camera_deps = """
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.androidx.camera.core)
"""

content = content.replace('  implementation(libs.mlkit.barcode)\\n', '  implementation(libs.mlkit.barcode)\\n' + camera_deps)

with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
