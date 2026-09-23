with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceTab.kt', 'r') as f:
    content = f.read()

import_statement = """import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
"""

content = content.replace("import com.example.ui.components.CrescentCard", import_statement + "import com.example.ui.components.CrescentCard")

gps_ui = """
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
"""

content = content.replace("""            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (uiState.isSessionExisting) "Edit Attendance" else "New Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isSessionExisting) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { viewModel.markAllPresent() }) {
                    Text("Mark All Present")
                }
            }""", gps_ui)

with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceTab.kt', 'w') as f:
    f.write(content)

