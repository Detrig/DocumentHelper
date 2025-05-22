package com.example.documenthelper.documents.presentation.documentsmain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.usecase.GetAllDocumentsUseCase
import com.example.documenthelper.documents.domain.usecase.SaveDocumentEntityUseCase
import com.example.documenthelper.documents.domain.usecase.SaveOpenedDocumentPathUseCase
import com.example.documenthelper.documents.domain.utils.livedata.DocumentsUiStateLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.CurrentDocumentLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.FillDocumentScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentsViewModel(
    private val navigation: Navigation,
    private val documentsUiStateLiveDataWrapper: DocumentsUiStateLiveDataWrapper,
    private val clickedDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper,
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val saveOpenedDocumentPathUseCase: SaveOpenedDocumentPathUseCase,
    private val saveDocumentEntityUseCase: SaveDocumentEntityUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun fillDocumentScreen(document: DocumentEntity) {
        clickedDocumentLiveDataWrapper.update(document)
        navigation.update(FillDocumentScreen)
    }

    fun loadDocuments() {
        viewModelScope.launch(dispatcherIO) {
            withContext(dispatcherMain) {
                documentsUiStateLiveDataWrapper.update(DocumentsUiState.Loading)
            }

            val documentList = getAllDocumentsUseCase.invoke()
            Log.d("DH-01", "documents: $documentList")
            withContext(dispatcherMain) {
                if (documentList.isNotEmpty()) {
                    documentsUiStateLiveDataWrapper.update(DocumentsUiState.Success(documentList))
                } else {
                    documentsUiStateLiveDataWrapper.update(DocumentsUiState.EmptyDocumentList)
                }
            }
        }
    }

    fun documentLiveData() = documentsUiStateLiveDataWrapper.liveData()

    fun saveDocUri(uri: String) {
        Log.d("DH-Uri", "Uri saved: $uri")
        saveOpenedDocumentPathUseCase.invoke(uri)
    }

    fun saveDocEntity(documentEntity: DocumentEntity) {
        viewModelScope.launch(dispatcherIO) {
            saveDocumentEntityUseCase.invoke(documentEntity)
            loadDocuments()
        }
    }
}


