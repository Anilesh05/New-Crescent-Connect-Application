package com.example.data.storage

import com.example.BuildConfig
import com.example.domain.repository.FileStorageService
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.File
import kotlin.time.Duration.Companion.hours

class SupabaseStorageServiceImpl : FileStorageService {
    private val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Storage)
    }

    private val bucket = supabase.storage.from("crescentconnect-files")

    override suspend fun upload(localPath: String, remotePath: String, mimeType: String): Result<String> {
        return try {
            val file = File(localPath)
            bucket.upload(remotePath, file.readBytes()) { upsert = false }
            val url = bucket.createSignedUrl(remotePath, 1.hours)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun download(remotePath: String, localPath: String): Result<Unit> {
        return try {
            val bytes = bucket.downloadAuthenticated(remotePath)
            File(localPath).writeBytes(bytes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(remotePath: String): Result<Unit> {
        return try {
            bucket.delete(remotePath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUrl(remotePath: String): Result<String> {
        return try {
            val url = bucket.createSignedUrl(remotePath, 1.hours)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
