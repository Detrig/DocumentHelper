package com.example.documenthelper.documents.presentation.filldocument

import com.example.documenthelper.documents.data.room.DocumentEntity

sealed interface DocumentFillUiState {
    data class Error(val message: String) : DocumentFillUiState
    data class PlaceHoldersExist(val document: DocumentEntity) : DocumentFillUiState
    object PlaceHoldersDoesntExist : DocumentFillUiState
}