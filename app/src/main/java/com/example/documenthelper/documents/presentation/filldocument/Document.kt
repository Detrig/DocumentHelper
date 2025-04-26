package com.example.documenthelper.documents.presentation.filldocument

import android.net.Uri

data class Document(
    val tempFile : Uri,
    val placeholders: List<String>
)