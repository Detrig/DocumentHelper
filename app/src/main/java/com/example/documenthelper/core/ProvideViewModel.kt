package com.example.documenthelper.core

import androidx.lifecycle.ViewModel
import com.example.documenthelper.MainViewModel
import com.example.documenthelper.documents.data.repo.DocumentRepositoryImpl
import com.example.documenthelper.documents.data.room.DocumentDao
import com.example.documenthelper.documents.data.sharedprefs.SharedPrefsLastOpenedDocumentDataSource
import com.example.documenthelper.documents.domain.DocumentRepository
import com.example.documenthelper.documents.domain.LastOpenedDocumentDataSource
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsViewModel
import com.example.documenthelper.documents.presentation.filldocument.CurrentDocumentLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.FillDocumentViewModel
import com.example.documenthelper.documents.presentation.preview.DocumentPreviewViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface ProvideViewModel {

    fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T

    class Base(
        private val clear: ClearViewModel,
        private val documentDao: DocumentDao,
        private val lastOpenedDocumentDataSource: LastOpenedDocumentDataSource,
    ) : ProvideViewModel {
        private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val navigation = Navigation.Base()

        private val currentDocumentLiveDataWrapper = CurrentDocumentLiveDataWrapper.Base()

        private val documentRepository = DocumentRepositoryImpl(documentDao, lastOpenedDocumentDataSource)

        override fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T {
            return when (viewModelClass) {
                MainViewModel::class.java -> MainViewModel(navigation)
                DocumentsViewModel::class.java -> DocumentsViewModel(
                    navigation,
                    currentDocumentLiveDataWrapper,
                    App.database.documentDao(),
                    viewModelScope
                )

                FillDocumentViewModel::class.java -> FillDocumentViewModel(
                    navigation,
                    currentDocumentLiveDataWrapper
                )

                DocumentPreviewViewModel::class.java -> DocumentPreviewViewModel(
                    navigation,
                    documentRepository
                )

                else -> throw IllegalStateException("unknown viewModelClass $viewModelClass")
            } as T

        }
    }
}