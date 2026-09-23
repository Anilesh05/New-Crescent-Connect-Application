sed -i 's/\/\/ implementation(libs.firebase.firestore)/implementation(libs.firebase.firestore)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.firebase.auth)/implementation(libs.firebase.auth)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.credentials)/implementation(libs.androidx.credentials)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.credentials.play.services)/implementation(libs.androidx.credentials.play.services)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.googleid)/implementation(libs.googleid)/' app/build.gradle.kts

echo "workRuntimeKtx = \"2.10.0\"" >> gradle/libs.versions.toml
echo "androidx-work-runtime-ktx = { group = \"androidx.work\", name = \"work-runtime-ktx\", version.ref = \"workRuntimeKtx\" }" >> gradle/libs.versions.toml
echo "implementation(libs.androidx.work.runtime.ktx)" >> app/build.gradle.kts

