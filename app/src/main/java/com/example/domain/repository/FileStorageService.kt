package com.example.domain.repository

interface FileStorageService {
    suspend fun upload(localPath: String, remotePath: String, mimeType: String): Result<String>
    suspend fun download(remotePath: String, localPath: String): Result<Unit>
    suspend fun delete(remotePath: String): Result<Unit>
    suspend fun getUrl(remotePath: String): Result<String>
}
