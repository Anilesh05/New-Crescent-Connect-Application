with open('app/src/main/java/com/example/domain/repository/Repositories.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>)',
    'suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>)\n    suspend fun saveSession(session: AttendanceSession)\n    suspend fun saveRecord(record: AttendanceRecord)'
)

with open('app/src/main/java/com/example/domain/repository/Repositories.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt', 'r') as f:
    content2 = f.read()

impl_add = """
    override suspend fun saveSession(session: AttendanceSession) {
        attendanceDao.insertSession(session.toEntity())
    }

    override suspend fun saveRecord(record: AttendanceRecord) {
        attendanceDao.insertRecords(listOf(record.toEntity()))
    }
"""

content2 = content2.replace('override suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>) {', impl_add + '\n    override suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>) {')

with open('app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt', 'w') as f:
    f.write(content2)
