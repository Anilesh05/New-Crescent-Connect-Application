package com.example.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AttendanceStatus
import com.example.ui.components.CrescentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CRAttendanceTab(viewModel: CRAttendanceViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("CR Authorized Attendance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (uiState.activePermissions.isEmpty()) {
            Text("No active attendance permissions available.", style = MaterialTheme.typography.bodyMedium)
            return
        }

        // Selection Controls
        CrescentCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Authorized Session", style = MaterialTheme.typography.labelMedium)
                uiState.activePermissions.forEach { permission ->
                    FilterChip(
                        selected = uiState.selectedPermission?.id == permission.id,
                        onClick = { viewModel.selectPermission(permission) },
                        label = { Text("Course: ${permission.courseId} | Period: ${permission.periodNumber} | Date: ${permission.date}") }
                    )
                }
            }
        }
        
        uiState.errorMessage?.let { msg ->
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.selectedPermission != null && uiState.errorMessage == null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mark Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.enrolledStudents) { state ->
                    StudentAttendanceRow(
                        state = state,
                        onStatusChange = { viewModel.markStudent(state.student.id, it) }
                    )
                }
            }

            Button(
                onClick = { viewModel.submitAttendance() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSubmitted
            ) {
                Text(if (uiState.isSubmitted) "Submitted Successfully" else "Submit Attendance")
            }
        }
    }
}
