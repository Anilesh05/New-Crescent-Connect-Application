import re

def add_academic_enqueue(file_path):
    with open(file_path, 'r') as f:
        content = f.read()
    
    enqueue_code = """
        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AcademicSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
"""
    # Just insert it before closing braces of mutate methods
    for method in ["createAnnouncement", "createStudyMaterial", "createAssignment", "markAssignmentCompleted"]:
        pattern = r'(override suspend fun ' + method + r'\(.*?\)\s*\{.*?)(^\s*\})'
        content = re.sub(pattern, r'\1' + enqueue_code + r'\2', content, flags=re.DOTALL | re.MULTILINE)
        
    with open(file_path, 'w') as f:
        f.write(content)

def add_chat_enqueue(file_path):
    with open(file_path, 'r') as f:
        content = f.read()
    
    enqueue_code = """
        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.ChatSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
"""
    pattern = r'(override suspend fun sendMessage\(.*?\)\s*\{.*?)(^\s*\})'
    content = re.sub(pattern, r'\1' + enqueue_code + r'\2', content, flags=re.DOTALL | re.MULTILINE)
        
    with open(file_path, 'w') as f:
        f.write(content)

add_academic_enqueue('app/src/main/java/com/example/data/repository/AcademicRepositoryImpl.kt')
add_chat_enqueue('app/src/main/java/com/example/data/repository/ChatRepositoryImpl.kt')
