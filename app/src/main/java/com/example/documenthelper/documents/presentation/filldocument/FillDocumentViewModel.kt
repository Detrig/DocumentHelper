package com.example.documenthelper.documents.presentation.filldocument

import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsScreen

class FillDocumentViewModel(
    private val navigation: Navigation,
    private val currentDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper
) : ViewModel() {

//    suspend fun generateDocument(fieldValues: Map<String, String>): File {
//        return documentRepository.generateFilledDocument(
//            outputDirectory = fileStorage.getOutputDirectory(),
//            fieldValues = fieldValues
//        )
//    }

    fun documentsScreen() {
        navigation.update(DocumentsScreen)
    }

    fun currentDocLiveData() = currentDocumentLiveDataWrapper.liveData()
}
