package com.example.documenthelper.documents.data


import android.content.Context
import java.io.File

interface FileDataSource {
    suspend fun saveToTempFile(fileName: String, content: ByteArray): File
    suspend fun getTempFile(): File?
}

class AndroidFileDataSource(private val context: Context) : FileDataSource {
    override suspend fun saveToTempFile(fileName: String, content: ByteArray): File {
        val dir = context.cacheDir.resolve("temp_documents").apply { mkdirs() }
        val file = File(dir, fileName)
        file.writeBytes(content)
        return file
    }

    override suspend fun getTempFile(): File? {
        val dir = context.cacheDir.resolve("temp_documents")
        return dir.listFiles()?.firstOrNull()
    }
}