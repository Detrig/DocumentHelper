package com.example.documenthelper.documents.domain.usecase

import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.DocumentRepository

class SaveDocumentEntityUseCase(private val documentRepository: DocumentRepository) {
    suspend operator fun invoke(documentEntity: DocumentEntity) {
        documentRepository.insertDocument(documentEntity)
    }
}