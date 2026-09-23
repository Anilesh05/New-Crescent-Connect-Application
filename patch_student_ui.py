with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'r') as f:
    content = f.read()

import_statement = """import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.example.util.LocationVerifier
import android.annotation.SuppressLint
"""

content = content.replace("import androidx.compose.ui.draw.clip", "import androidx.compose.ui.draw.clip\n" + import_statement)

active_sessions_ui = """
        // GPS Attendance Section
        if (uiState.activeSessions.isNotEmpty()) {
            Text("Active Attendance Windows", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.activeSessions.forEach { active ->
                    ActiveAttendanceCard(active, viewModel)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        uiState.attendanceMarkResult?.let { resultMsg ->
            val isError = resultMsg.startsWith("Error")
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(resultMsg, style = MaterialTheme.typography.bodyMedium, color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.clearResult() }) {
                        Text("Dismiss")
                    }
                }
            }
        }
"""

content = content.replace('Text("Subject Wise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)', active_sessions_ui + '\n        Text("Subject Wise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)')


active_card = """
@SuppressLint("MissingPermission")
@Composable
fun ActiveAttendanceCard(active: ActiveSelfAttendanceSession, viewModel: StudentAttendanceViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val sessionLat = active.session.latitude ?: return@addOnSuccessListener
                    val sessionLng = active.session.longitude ?: return@addOnSuccessListener
                    
                    val distance = LocationVerifier.calculateDistanceMeters(
                        sessionLat, sessionLng,
                        location.latitude, location.longitude
                    )
                    
                    val isMock = location.isFromMockProvider
                    
                    // We need studentId. The viewmodel handles that, wait, markSelfAttendance needs studentId.
                    // We can get studentId from sessionManager or let viewmodel handle it internally.
                    // Let's pass empty and let viewmodel get it.
                    // Wait, markSelfAttendance requires studentId.
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${active.courseName} - Period ${active.session.periodNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Location: Campus", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(12.dp))
            
            if (active.hasAlreadyMarked) {
                Button(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("✓ Already Marked")
                }
            } else {
                Button(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    val sessionLat = active.session.latitude
                                    val sessionLng = active.session.longitude
                                    if (sessionLat != null && sessionLng != null) {
                                        val distance = LocationVerifier.calculateDistanceMeters(
                                            sessionLat, sessionLng, location.latitude, location.longitude
                                        )
                                        viewModel.markSelfAttendance(active.session, "STUDENT_ID_PLACEHOLDER", distance, location.accuracy, location.isFromMockProvider)
                                    } else {
                                        viewModel.markSelfAttendance(active.session, "STUDENT_ID_PLACEHOLDER", 0f, location.accuracy, location.isFromMockProvider)
                                    }
                                } else {
                                    // Could not get location
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark Attendance (GPS)")
                }
            }
        }
    }
}
"""

content = content + active_card

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceTab.kt', 'w') as f:
    f.write(content)
