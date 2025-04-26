package com.example.documenthelper.core

import androidx.lifecycle.ViewModel
import com.example.documenthelper.MainViewModel
import com.example.documenthelper.documents.documentsmain.DocumentsViewModel
import com.example.documenthelper.documents.filldocument.CurrentDocumentLiveDataWrapper
import com.example.documenthelper.documents.filldocument.FillDocumentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface ProvideViewModel {

    fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T

    class Base(
        private val clear: ClearViewModel
    ) : ProvideViewModel {
        private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val navigation = Navigation.Base()

        private val currentDocumentLiveDataWrapper = CurrentDocumentLiveDataWrapper.Base()

        override fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T {
            return when (viewModelClass) {
                MainViewModel::class.java -> MainViewModel(navigation)
                DocumentsViewModel::class.java -> DocumentsViewModel(navigation, currentDocumentLiveDataWrapper)
                FillDocumentViewModel::class.java -> FillDocumentViewModel(navigation, currentDocumentLiveDataWrapper)
                else -> throw IllegalStateException("unknown viewModelClass $viewModelClass")
            } as T

        }
    }
}