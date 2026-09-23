with open('app/build.gradle.kts', 'r') as f:
    content = f.read()
if "com.google.ai.client.generativeai:generativeai" not in content:
    content = content.replace('implementation(libs.firebase.ai)', 'implementation(libs.firebase.ai)\n    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")')
with open('app/build.gradle.kts', 'w') as f:
    f.write(content)
