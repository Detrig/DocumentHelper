package com.example.documenthelper.documents.domain.utils.livedata

import com.example.documenthelper.core.LiveDataWrapper
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsUiState

interface DocumentsUiStateLiveDataWrapper : LiveDataWrapper.Mutable<DocumentsUiState> {
    class Base: DocumentsUiStateLiveDataWrapper, LiveDataWrapper.Abstract<DocumentsUiState>()
}