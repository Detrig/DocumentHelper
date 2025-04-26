package com.example.documenthelper.documents.filldocument

import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.documentsmain.DocumentsScreen

class FillDocumentViewModel(
    private val navigation: Navigation,
    private val currentDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper
) : ViewModel() {

    fun documentsScreen() {
        navigation.update(DocumentsScreen)
    }

    fun currentDocLiveData() = currentDocumentLiveDataWrapper.liveData()
}
