package com.example.documenthelper.documents.presentation.editdocument

import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.presentation.filldocument.CurrentDocumentLiveDataWrapper

class EditDocumentViewModel(
    private val navigation: Navigation,
    private val currentDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper
) : ViewModel() {

    fun currentDocumentLiveData() = currentDocumentLiveDataWrapper.liveData()
}