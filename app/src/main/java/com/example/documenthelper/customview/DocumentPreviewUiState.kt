package com.example.documenthelper.customview

import java.io.Serializable

interface DocumentPreviewUiState : Serializable {

    fun update(documentPreviewView: DocumentPreviewView)

    abstract class Abstract(
        private val template: String,
        private val filledValues: HashMap<String, String>,
        private val highlight: Boolean
    ) : DocumentPreviewUiState {
        override fun update(documentPreviewView: DocumentPreviewView) {
            documentPreviewView.update(template, filledValues, highlight)
        }
    }

    data class HighlightValuesTrue(
        private val template: String,
        private val filledValues: HashMap<String, String>
    ) : Abstract(template, filledValues, true)

    data class HighlightValuesFalse(
        private val template: String,
        private val filledValues: HashMap<String, String>
    ) : Abstract(template, filledValues, false)

    object Empty : DocumentPreviewUiState {
        override fun update(documentPreviewView: DocumentPreviewView) {
            documentPreviewView.update("", hashMapOf(), false)
        }
    }
}