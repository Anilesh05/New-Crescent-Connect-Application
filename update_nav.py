import re

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

# Update Route.CourseChat
new_route = """    @Serializable data class CourseChat(val courseId: String, val courseName: String, val courseCode: String) : Route()"""
content = re.sub(r'@Serializable data object CourseChat : Route\(\)', new_route, content)

# Update StudentDashboard navigation callback
content = content.replace(
    'onNavigateToCourseChat = { navController.navigate(Route.CourseChat) }',
    'onNavigateToCourseChat = { courseId, name, code -> navController.navigate(Route.CourseChat(courseId, name, code)) }'
)

# Update AppNavigation CourseChat composable
new_composable = """        composable<Route.CourseChat> { backStackEntry ->
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
        }"""
content = re.sub(r'composable<Route\.CourseChat> \{.*?CourseChatScreen\(onBack = \{ navController\.popBackStack\(\) \}\).*?\}', new_composable, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
