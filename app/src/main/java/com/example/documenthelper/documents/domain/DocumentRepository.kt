package com.example.documenthelper.documents.domain

import com.example.documenthelper.documents.data.room.DocumentEntity

interface DocumentRepository {
    suspend fun getAllDocuments() : List<DocumentEntity>
    suspend fun insertDocument(entity: DocumentEntity)
    suspend fun updateDocument(entity: DocumentEntity)
    suspend fun deleteDocument(entity: DocumentEntity)
    suspend fun getById(id: Int): DocumentEntity?

    // Сохранить путь к последнему открытому документу (например, при вылете)
    fun saveOpenedDocumentPath(uri: String)
    fun getLastOpenedDocumentPath(): String?
    fun clearLastOpenedDocumentPath()
}