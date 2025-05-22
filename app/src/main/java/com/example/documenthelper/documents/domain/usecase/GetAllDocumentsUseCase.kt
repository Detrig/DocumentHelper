package com.example.documenthelper.documents.domain.usecase

import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.DocumentRepository

class GetAllDocumentsUseCase(private val documentRepository: DocumentRepository) {
    suspend operator fun invoke() : List<DocumentEntity> =
        documentRepository.getAllDocuments()
}