package com.example.documenthelper.documents.data.repo

import com.example.documenthelper.documents.data.FileDataSource
import com.example.documenthelper.documents.data.room.DocumentDao
import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.DocumentRepository
import com.example.documenthelper.documents.domain.LastOpenedDocumentDataSource

class DocumentRepositoryImpl(
    private val dao: DocumentDao,
    //private val fileDataSource: FileDataSource,
    private val openedDocDataSource: LastOpenedDocumentDataSource
) : DocumentRepository {
    override suspend fun getAllDocuments() = dao.getAll()
    override suspend fun insertDocument(entity: DocumentEntity) = dao.insert(entity)
    override suspend fun updateDocument(entity: DocumentEntity) = dao.update(entity)
    override suspend fun deleteDocument(entity: DocumentEntity) = dao.delete(entity)
    override suspend fun getById(id: Int) = dao.getById(id)

    override fun saveOpenedDocumentPath(uri: String) {
        openedDocDataSource.save(uri)
    }

    override fun getLastOpenedDocumentPath(): String? {
        return openedDocDataSource.get()
    }

    override fun clearLastOpenedDocumentPath() {
        openedDocDataSource.clear()
    }
}