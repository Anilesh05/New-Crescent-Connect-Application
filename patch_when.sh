sed -i 's/when (selectedItem) {/when {/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i 's/0 -> StudentHomeTab(/selectedItem == 0 -> StudentHomeTab(/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i 's/1 -> StudentAttendanceTab/selectedItem == 1 -> StudentAttendanceTab/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i 's/2 -> StudentAcademicsTab()/selectedItem == 2 -> StudentAcademicsTab()/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i 's/3 -> StudentMessagesTab/selectedItem == 3 -> StudentMessagesTab/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i 's/4 -> ProfileTab(/selectedItem == items.lastIndex -> ProfileTab(/' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
sed -i '/selectedItem == items.lastIndex -> ProfileTab(/i\                    selectedItem == 4 && isCR -> {\n                        if (crAttendanceViewModel != null) CRAttendanceTab(crAttendanceViewModel) else Text("Loading CR...")\n                    }' app/src/main/java/com/example/ui/dashboard/StudentDashboard.kt
