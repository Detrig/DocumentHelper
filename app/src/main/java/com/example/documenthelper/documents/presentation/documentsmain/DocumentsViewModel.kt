package com.example.documenthelper.documents.presentation.documentsmain

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.data.room.DocumentDao
import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.presentation.filldocument.CurrentDocumentLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.FillDocumentScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocumentsViewModel(
    private val navigation: Navigation,
    private val currentDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper,
    private val documentDao: DocumentDao,
    private val viewModelScope : CoroutineScope,
    private val dispatcherIO : CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun fillDocumentScreen(templatePath: String, placeholders: List<String>) {
//        currentDocumentLiveDataWrapper.update(
//            Document(templatePath, placeholders)
//        )
        Log.d("Document", "path: $templatePath placeHolder: ${placeholders.toString()}")
        navigation.update(FillDocumentScreen)
    }

    fun currentDocLiveData() = currentDocumentLiveDataWrapper.liveData()

    fun updateDocLiveData(value : DocumentEntity) {
        currentDocumentLiveDataWrapper.update(value)
    }

    fun insertDocument(document: DocumentEntity) {
        viewModelScope.launch(dispatcherIO) {
            documentDao.insert(document)
        }
    }
}


