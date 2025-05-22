package com.example.documenthelper.core

import androidx.lifecycle.ViewModel
import com.example.documenthelper.MainViewModel
import com.example.documenthelper.documents.data.repo.DocumentRepositoryImpl
import com.example.documenthelper.documents.data.room.DocumentDao
import com.example.documenthelper.documents.domain.LastOpenedDocumentDataSource
import com.example.documenthelper.documents.domain.usecase.GetAllDocumentsUseCase
import com.example.documenthelper.documents.domain.usecase.SaveDocumentEntityUseCase
import com.example.documenthelper.documents.domain.usecase.SaveOpenedDocumentPathUseCase
import com.example.documenthelper.documents.domain.utils.livedata.DocumentsUiStateLiveDataWrapper
import com.example.documenthelper.documents.domain.utils.livedata.FilledValuesLiveDataWrapper
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsViewModel
import com.example.documenthelper.documents.presentation.filldocument.AttachmentsLiveDataWrapper
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
        private val filledValuesLiveDataWrapper = FilledValuesLiveDataWrapper.Base()

        private val documentsUiStateLiveDataWrapper = DocumentsUiStateLiveDataWrapper.Base()
        private val documentRepository =
            DocumentRepositoryImpl(documentDao, lastOpenedDocumentDataSource)

        private val getAllDocumentsUseCase = GetAllDocumentsUseCase(documentRepository)
        private val saveOpenedDocumentPathUseCase =
            SaveOpenedDocumentPathUseCase(documentRepository)
        private val saveDocumentEntityUseCase = SaveDocumentEntityUseCase(documentRepository)

        private val attachmentsLiveDataWrapper = AttachmentsLiveDataWrapper.Base()

        override fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T {
            return when (viewModelClass) {
                MainViewModel::class.java -> MainViewModel(navigation)
                DocumentsViewModel::class.java -> DocumentsViewModel(
                    navigation,
                    documentsUiStateLiveDataWrapper,
                    currentDocumentLiveDataWrapper,
                    getAllDocumentsUseCase,
                    saveOpenedDocumentPathUseCase,
                    saveDocumentEntityUseCase,
                    viewModelScope
                )

                FillDocumentViewModel::class.java -> FillDocumentViewModel(
                    navigation,
                    currentDocumentLiveDataWrapper,
                    attachmentsLiveDataWrapper,
                    filledValuesLiveDataWrapper
                )

                DocumentPreviewViewModel::class.java -> DocumentPreviewViewModel(
                    navigation,
                    currentDocumentLiveDataWrapper,
                    attachmentsLiveDataWrapper,
                    filledValuesLiveDataWrapper
                )

                else -> throw IllegalStateException("unknown viewModelClass $viewModelClass")
            } as T

        }
    }
}