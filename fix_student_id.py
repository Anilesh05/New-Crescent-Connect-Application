with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'fun markSelfAttendance(session: AttendanceSession, studentId: String, distance: Float, accuracy: Float, isMock: Boolean) {',
    'fun markSelfAttendance(session: AttendanceSession, distance: Float, accuracy: Float, isMock: Boolean) {'
)

content = content.replace(
    'val record = AttendanceRecord(',
    '''val userId = sessionManager.userIdFlow.firstOrNull() ?: return@launch
            val record = AttendanceRecord('''
)
content = content.replace(
    'studentId = studentId,',
    'studentId = userId,'
)

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'viewModel.markSelfAttendance(active.session, "STUDENT_ID_PLACEHOLDER", distance, location.accuracy, location.isFromMockProvider)',
    'viewModel.markSelfAttendance(active.session, distance, location.accuracy, location.isFromMockProvider)'
)
content = content.replace(
    'viewModel.markSelfAttendance(active.session, "STUDENT_ID_PLACEHOLDER", 0f, location.accuracy, location.isFromMockProvider)',
    'viewModel.markSelfAttendance(active.session, 0f, location.accuracy, location.isFromMockProvider)'
)

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'w') as f:
    f.write(content)
