import re

with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

# Add versions
if 'supabaseBom' not in content:
    content = content.replace('[versions]', '[versions]\nsupabaseBom = "3.0.0"\nktor = "3.0.0"\n')

# Add libraries
if 'supabase-bom' not in content:
    content = content.replace('[libraries]', '[libraries]\nsupabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabaseBom" }\nsupabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt" }\nktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }\n')

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

with open('app/build.gradle.kts', 'r') as f:
    build = f.read()

if 'supabase.bom' not in build:
    build = build.replace('dependencies {', 'dependencies {\n  implementation(platform(libs.supabase.bom))\n  implementation(libs.supabase.storage)\n  implementation(libs.ktor.client.android)\n')
    
with open('app/build.gradle.kts', 'w') as f:
    f.write(build)
