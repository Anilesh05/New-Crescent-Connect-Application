sed -i '135,146s/    syncStatus = "PENDING"/    /' app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt
sed -i 's/    isActive = isActive,     /    isActive = isActive/' app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt
