package com.example.documenthelper.documents.documentsmain

import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.filldocument.CurrentDocumentLiveDataWrapper
import com.example.documenthelper.documents.filldocument.Document
import com.example.documenthelper.documents.filldocument.FillDocumentScreen

class DocumentsViewModel(
    private val navigation: Navigation,
    private val currentDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper
) : ViewModel() {

    fun fillDocumentScreen(templatePath: String, placeholders: List<String>) {
        currentDocumentLiveDataWrapper.update(
            Document(templatePath, placeholders)
        )
        navigation.update(FillDocumentScreen)
    }

    fun currentDocLiveData() = currentDocumentLiveDataWrapper.liveData()
}


