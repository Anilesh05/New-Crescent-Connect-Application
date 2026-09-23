with open('app/src/main/java/com/example/data/local/DatabaseSeeder.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'AttendanceSessionEntity("S1", "C1", "2026-08-28", 2, "U2", "STAFF", SyncStatus.SYNCED.name, System.currentTimeMillis() - 172800000)',
    'AttendanceSessionEntity("S1", "C1", "2026-08-28", 2, "U2", "STAFF", SyncStatus.SYNCED.name, System.currentTimeMillis() - 172800000)'
)

# Wait, we added default values to AttendanceSessionEntity, so we don't strictly need to modify the constructor calls in DatabaseSeeder if it uses positional arguments? Let's check DatabaseSeeder.kt
