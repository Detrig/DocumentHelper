package com.example.documenthelper.documents.presentation.documentsmain

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.documenthelper.documents.data.room.DocumentEntity

sealed interface DocumentsUiState {
    object Loading : DocumentsUiState
    data class Error(val message: String) : DocumentsUiState
    data class Success(val documentList: List<DocumentEntity>) : DocumentsUiState
    object EmptyDocumentList : DocumentsUiState
}