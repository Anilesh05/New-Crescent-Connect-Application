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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.components.CrescentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAttendanceTab(viewModel: StaffAttendanceViewModel) {
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
        Text("Mark Attendance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (uiState.courses.isEmpty()) {
            Text("No classes assigned to you.")
            return
        }

        // Selection Controls
        CrescentCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Simplified selection using segmented buttons or simple rows since Dropdown can be verbose in Compose
                Text("Select Class", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.courses.forEach { course ->
                        FilterChip(
                            selected = uiState.selectedCourse?.id == course.id,
                            onClick = { viewModel.selectCourse(course) },
                            label = { Text(course.subjectName) }
                        )
                    }
                }

                if (uiState.selectedCourse != null) {
                    Text("Select Period", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..6).forEach { period ->
                            FilterChip(
                                selected = uiState.selectedPeriod == period,
                                onClick = { viewModel.selectPeriod(period) },
                                label = { Text("P$period") }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.selectedCourse != null) {

            var showGpsConfig by remember { mutableStateOf(false) }
            var latText by remember { mutableStateOf("12.8797") }
            var lngText by remember { mutableStateOf("80.0811") }
            var radiusText by remember { mutableStateOf("100") }
            var durationText by remember { mutableStateOf("50") }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (uiState.isSessionExisting) "Edit Attendance" else "New Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isSessionExisting) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                )
                Row {
                    TextButton(onClick = { showGpsConfig = !showGpsConfig }) {
                        Text("GPS Mode")
                    }
                    TextButton(onClick = { viewModel.markAllPresent() }) {
                        Text("Mark All Present")
                    }
                }
            }
            
            if (showGpsConfig && !uiState.isSessionExisting) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Configure GPS Attendance Window", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = latText, onValueChange = { latText = it }, label = { Text("Latitude") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = lngText, onValueChange = { lngText = it }, label = { Text("Longitude") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = radiusText, onValueChange = { radiusText = it }, label = { Text("Radius (m)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = durationText, onValueChange = { durationText = it }, label = { Text("Duration (mins)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        Button(onClick = {
                            val lat = latText.toDoubleOrNull() ?: 0.0
                            val lng = lngText.toDoubleOrNull() ?: 0.0
                            val rad = radiusText.toIntOrNull() ?: 100
                            val dur = durationText.toIntOrNull() ?: 50
                            viewModel.startSelfAttendanceWindow(dur, lat, lng, rad)
                            showGpsConfig = false
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Start GPS Session")
                        }
                    }
                }
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
            
            if (uiState.isSessionExisting && !uiState.isSubmitted) {
                Text("Warning: You are editing an existing attendance record.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun StudentAttendanceRow(state: StudentAttendanceState, onStatusChange: (AttendanceStatus) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(state.student.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(state.student.registerNumber ?: "", style = MaterialTheme.typography.labelSmall)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.status == AttendanceStatus.PRESENT,
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) },
                    label = { Text("P") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer)
                )
                FilterChip(
                    selected = state.status == AttendanceStatus.ABSENT,
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) },
                    label = { Text("A") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}
