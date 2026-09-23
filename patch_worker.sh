sed -i 's/val firestore = FirebaseFirestore.getInstance()/\
            val firestore = try {\
                FirebaseFirestore.getInstance()\
            } catch (e: IllegalStateException) {\
                Log.e("SyncWorker", "Firebase not initialized. Cannot sync to Firestore. Please add google-services.json.", e)\
                return Result.failure()\
            }/' app/src/main/java/com/example/data/sync/AttendanceSyncWorker.kt
