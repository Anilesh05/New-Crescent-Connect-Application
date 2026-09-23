with open('app/src/main/java/com/example/data/local/dao/AttendanceDao.kt', 'r') as f:
    content = f.read()

new_query = """    @Query("SELECT * FROM cr_permissions WHERE syncStatus = \\"PENDING\\"")
    suspend fun getPendingPermissions(): List<CRPermissionEntity>
    
    @Query("SELECT * FROM attendance_records WHERE syncStatus = \\"PENDING\\"")
    suspend fun getPendingRecords(): List<AttendanceRecordEntity>"""

content = content.replace('    @Query("SELECT * FROM cr_permissions WHERE syncStatus = \\"PENDING\\"")\n    suspend fun getPendingPermissions(): List<CRPermissionEntity>', new_query)

with open('app/src/main/java/com/example/data/local/dao/AttendanceDao.kt', 'w') as f:
    f.write(content)
