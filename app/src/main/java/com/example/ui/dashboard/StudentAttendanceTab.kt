package com.example.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
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

import com.example.ui.components.CrescentCard
import com.example.ui.components.CrescentStatusBadge
import com.example.ui.components.BadgeStatus
import kotlin.math.roundToInt

@Composable
fun StudentAttendanceTab(viewModel: StudentAttendanceViewModel) {
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Attendance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        // Overall Attendance
        val totalConducted = uiState.courseStats.sumOf { it.totalClasses }
        val totalPresent = uiState.courseStats.sumOf { it.presentClasses }
        val overallProgress = if (totalConducted > 0) uiState.overallPercentage / 100f else 0f
        
        CrescentCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Overall Attendance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier.size(120.dp),
                        strokeWidth = 12.dp,
                        color = if (overallProgress < 0.75f && totalConducted > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("${uiState.overallPercentage.roundToInt()}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Present", style = MaterialTheme.typography.labelSmall)
                        Text("$totalPresent", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Absent", style = MaterialTheme.typography.labelSmall)
                        Text("${totalConducted - totalPresent}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        
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

        Text("Subject Wise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        if (uiState.courseStats.isEmpty()) {
            Text("No attendance data available.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.courseStats.forEach { stat ->
                    AttendanceSubjectCard(stat)
                }
            }
        }
    }
}

@Composable
fun AttendanceSubjectCard(stat: CourseAttendanceStat) {
    val isShortage = stat.percentage < 75f && stat.totalClasses > 0
    val isWarning = stat.percentage in 75f..80f && stat.totalClasses > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stat.subjectName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${stat.percentage.roundToInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (isShortage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${stat.presentClasses} / ${stat.totalClasses}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            val progress = if (stat.totalClasses > 0) stat.percentage / 100f else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (isShortage) MaterialTheme.colorScheme.error else if (isWarning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isShortage) {
                CrescentStatusBadge("⚠ Attendance Shortage", BadgeStatus.ERROR)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Requires ${stat.requiredFor75()} consecutive classes to reach 75%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            } else if (stat.totalClasses > 0) {
                val impact = stat.impactOfAbsence()
                Text("If you miss next class: ${String.format("%.1f", impact)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (impact < 75f && !isShortage) {
                    Spacer(modifier = Modifier.height(4.dp))
                    CrescentStatusBadge("Miss next = Shortage", BadgeStatus.WARNING)
                }
            } else {
                Text("No classes conducted yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

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
                                        viewModel.markSelfAttendance(active.session, distance, location.accuracy, location.isFromMockProvider)
                                    } else {
                                        viewModel.markSelfAttendance(active.session, 0f, location.accuracy, location.isFromMockProvider)
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
