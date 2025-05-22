package com.example.documenthelper.documents.domain.usecase

import android.net.Uri
import com.example.documenthelper.documents.domain.DocumentRepository

class SaveOpenedDocumentPathUseCase(private val documentRepository: DocumentRepository) {
    operator fun invoke(uri: String) {
        documentRepository.saveOpenedDocumentPath(uri)
    }
}