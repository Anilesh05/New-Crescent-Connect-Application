sed -i '/fun getRecordsForSession(sessionId: String)/i\
    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")\n    suspend fun getRecordsForSessionList(sessionId: String): List<AttendanceRecordEntity>\n' app/src/main/java/com/example/data/local/dao/AttendanceDao.kt
