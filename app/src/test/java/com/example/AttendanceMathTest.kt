package com.example

import com.example.ui.dashboard.CourseAttendanceStat
import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceMathTest {

    @Test
    fun testAttendanceRiskLogic() {
        // We'll just test the math logic that would be output by AiAssistantViewModel
        fun getRisk(total: Int, present: Int): String {
            val percentage = if (total > 0) (present * 100f) / total else 0f
            return when {
                total == 0 -> "SAFE"
                percentage >= 75 -> "SAFE"
                percentage >= 65 -> "WARNING"
                else -> "CRITICAL"
            }
        }

        assertEquals("SAFE", getRisk(0, 0))
        assertEquals("CRITICAL", getRisk(1, 0))
        assertEquals("SAFE", getRisk(1, 1))
        assertEquals("WARNING", getRisk(3, 2)) // 66.6%
        assertEquals("SAFE", getRisk(4, 3)) // 75%
        assertEquals("SAFE", getRisk(8, 6)) // 75%
        assertEquals("WARNING", getRisk(10, 7)) // 70%
    }

    @Test
    fun testRequiredClassesCalculation() {
        fun getRequired(total: Int, present: Int): Int {
            val percentage = if (total > 0) (present * 100f) / total else 0f
            if (percentage >= 75f || total == 0) return 0
            return (3 * total - 4 * present).coerceAtLeast(0)
        }

        assertEquals(0, getRequired(0, 0))
        assertEquals(3, getRequired(1, 0))
        assertEquals(0, getRequired(1, 1))
        assertEquals(1, getRequired(3, 2)) // needs 1 more to be 3/4 = 75%
        assertEquals(0, getRequired(4, 3))
        assertEquals(0, getRequired(8, 6))
        assertEquals(2, getRequired(10, 7)) // 7/10 -> 8/11 (72) -> 9/12 (75%)
    }
}
