package com.example.ui.navigation

import com.example.ui.verification.QrScannerScreen
import com.example.ui.verification.ScanResultScreen
import com.example.ui.dashboard.AiAssistantViewModel
import com.example.data.repository.AiServiceImpl
import com.example.ui.dashboard.DigitalIDViewModel
import com.example.ui.dashboard.NotificationsViewModel
import com.example.ui.dashboard.NotificationsScreen
import androidx.navigation.toRoute

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.CrescentDatabase
import com.example.data.local.SessionManager
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.CourseRepositoryImpl
import com.example.data.repository.TimetableRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.data.repository.AttendanceRepositoryImpl
import com.example.domain.model.Role
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.LoginViewModel
import com.example.ui.dashboard.*
import com.example.ui.dashboard.StaffDashboardViewModel
import com.example.ui.dashboard.UserDashboardViewModel
import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable data object Login : Route()
    @Serializable data object AdminDashboard : Route()
    @Serializable data object StaffDashboard : Route()
    @Serializable data object StudentDashboard : Route()
    @Serializable data object DigitalID : Route()
    @Serializable data object ScanID : Route()
    @Serializable data class ScanResult(val qrPayload: String) : Route()
    @Serializable data object AiAssistant : Route()
    @Serializable data object Notifications : Route()
        @Serializable data class CourseChat(val courseId: String, val courseName: String, val courseCode: String) : Route()
    @Serializable data object ClassAdviserDashboard : Route()
}

@Composable
fun CrescentConnectApp(modifier: Modifier = Modifier, context: Context) {
    val navController = rememberNavController()
    
    val db = remember { CrescentDatabase.getDatabase(context) }
    val sessionManager = remember { SessionManager(context) }
    
    val authRepository = remember { AuthRepositoryImpl(db.userDao(), sessionManager) }
    val userRepository = remember { UserRepositoryImpl(db.userDao()) }
    val courseRepository = remember { CourseRepositoryImpl(db.courseDao()) }
    val timetableRepository = remember { TimetableRepositoryImpl(db.timetableDao()) }
    val attendanceRepository = remember { AttendanceRepositoryImpl(db.attendanceDao(), context) }
    val academicRepository = remember { com.example.data.repository.AcademicRepositoryImpl(db.academicDao(), context) }

    val sessionUserId by authRepository.getSessionUserId().collectAsState(initial = null)
    var startDestination: Route by remember { mutableStateOf(Route.Login) }
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AttendanceSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }

    LaunchedEffect(sessionUserId) {
        if (sessionUserId != null) {
            val user = userRepository.getUserById(sessionUserId!!)
            if (user != null) {
                startDestination = when (user.role) {
                    Role.ADMIN -> Route.AdminDashboard
                    Role.STAFF -> Route.StaffDashboard
                    Role.STUDENT, Role.CR -> Route.StudentDashboard
                    Role.CLASS_ADVISER -> Route.ClassAdviserDashboard
                }
            } else {
                startDestination = Route.Login
            }
        } else {
            startDestination = Route.Login
        }
        isReady = true
    }

    if (!isReady) return

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Login> {
            val viewModel = remember { LoginViewModel(authRepository) }
            LoginScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { role ->
                    val route = when (role) {
                        Role.ADMIN -> Route.AdminDashboard
                        Role.STAFF -> Route.StaffDashboard
                        Role.STUDENT, Role.CR -> Route.StudentDashboard
                        Role.CLASS_ADVISER -> Route.ClassAdviserDashboard
                    }
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.ClassAdviserDashboard> {
            val viewModel = remember { UserDashboardViewModel(userRepository, sessionManager) }
            ClassAdviserDashboard(
                viewModel = viewModel,
                sessionManager = sessionManager,
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }

        composable<Route.AdminDashboard> {
            val viewModel = remember { UserDashboardViewModel(userRepository, sessionManager) }
            AdminDashboard(
                viewModel = viewModel,
                sessionManager = sessionManager,
                onNavigateToDigitalID = { navController.navigate(Route.DigitalID) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }
        
        composable<Route.StaffDashboard> {
            val staffAttendanceViewModel = remember { StaffAttendanceViewModel(attendanceRepository, courseRepository, userRepository, sessionManager) }
            val viewModel = remember { StaffDashboardViewModel(userRepository, courseRepository, sessionManager) }
            StaffDashboard(
                viewModel = viewModel,
                attendanceViewModel = staffAttendanceViewModel, 
                sessionManager = sessionManager,
                onNavigateToScanID = { navController.navigate(Route.ScanID) },
                onNavigateToDigitalID = { navController.navigate(Route.DigitalID) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                }
            )
        }
        
        composable<Route.StudentDashboard> {
            val viewModel = remember { StudentDashboardViewModel(userRepository, courseRepository, sessionManager) }
            val attendanceViewModel = remember { StudentAttendanceViewModel(attendanceRepository, courseRepository, sessionManager) }
            val crAttendanceViewModel = remember { CRAttendanceViewModel(attendanceRepository, courseRepository, userRepository, sessionManager) }
            StudentDashboard(
                viewModel = viewModel,
                attendanceViewModel = attendanceViewModel,
                crAttendanceViewModel = crAttendanceViewModel,
                onNavigateToDigitalID = { navController.navigate(Route.DigitalID) },
                onNavigateToAiAssistant = { navController.navigate(Route.AiAssistant) },
                onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                onNavigateToCourseChat = { courseId, name, code -> navController.navigate(Route.CourseChat(courseId, name, code)) },
                onLogout = {
                    navController.navigate(Route.Login) { popUpTo(0) }
                },
                sessionManager = sessionManager
            )
        }

        
        composable<Route.ScanID> {
            QrScannerScreen(
                onBack = { navController.popBackStack() },
                onQrCodeDetected = { payload ->
                    navController.popBackStack()
                    navController.navigate(Route.ScanResult(payload))
                }
            )
        }
        
        composable<Route.ScanResult> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.ScanResult>()
            ScanResultScreen(
                payload = route.qrPayload,
                userRepository = userRepository,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Route.DigitalID> {
            val viewModel = remember { DigitalIDViewModel(userRepository, sessionManager) }
            DigitalIDScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable<Route.Notifications> {
            val viewModel = remember { NotificationsViewModel(sessionManager, academicRepository) }
            NotificationsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable<Route.AiAssistant> {
            val aiService = remember { AiServiceImpl("YOUR_API_KEY") }
            val viewModel = remember { AiAssistantViewModel(sessionManager, userRepository, courseRepository, attendanceRepository, aiService) }
            AiAssistantScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

                composable<Route.CourseChat> { backStackEntry ->
            val chatRoute = backStackEntry.toRoute<Route.CourseChat>()
            val chatRepo = remember { com.example.data.repository.ChatRepositoryImpl(db.chatDao(), context) }
            val chatViewModel = remember { com.example.ui.dashboard.CourseChatViewModel(chatRepo, userRepository, sessionManager) }
            
            LaunchedEffect(chatRoute.courseId) {
                chatViewModel.loadCourseChat(chatRoute.courseId)
            }
            
            CourseChatScreen(
                viewModel = chatViewModel,
                courseName = chatRoute.courseName,
                courseCode = chatRoute.courseCode,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
