package com.example.data.repository

import android.util.Log
import com.example.data.local.SessionManager
import com.example.data.local.dao.UserDao
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
    
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // 1. Authenticate with Firebase
            val firebaseAuth = FirebaseAuth.getInstance()
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // 2. We should fetch from Firestore, but for offline-first we can check Room first.
                // Or rather, the architecture says: Firebase Login -> Save local -> Open App
                // Since this is just the Auth repo, we'll try to find the user in local Room for now, 
                // and later the Sync Worker/Repository will fetch from Firestore and update Room.
                val userEntity = userDao.getUserByEmail(email)
                
                if (userEntity != null) {
                    sessionManager.saveUserId(userEntity.id)
                    Result.success(userEntity.toDomain())
                } else {
                    // Fallback to demo login if user isn't in Room yet (since we haven't synced Firestore to Room during login here)
                    Result.failure(Exception("User not found in local database. Please sync."))
                }
            } else {
                Result.failure(Exception("Firebase authentication failed (null user)"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase auth failed, falling back to local auth", e)
            
            // 3. OFFLINE FALLBACK OR DEMO ACCOUNT FALLBACK
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null && userEntity.passwordHash == password) {
                sessionManager.saveUserId(userEntity.id)
                Result.success(userEntity.toDomain())
            } else {
                val message = if (e is FirebaseAuthException) {
                    "Authentication Failed: ${e.message}. Note: Did you configure Firebase google-services.json?"
                } else {
                    "Invalid email or password (Offline)."
                }
                Result.failure(Exception(message))
            }
        }
    }

    override suspend fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase sign out error", e)
        }
        sessionManager.clearSession()
    }

    override fun getSessionUserId(): Flow<String?> {
        return sessionManager.userIdFlow
    }
}
