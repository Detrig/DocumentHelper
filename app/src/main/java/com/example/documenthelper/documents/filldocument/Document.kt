package com.example.documenthelper.documents.filldocument

data class Document(
    val tempFile : String,
    val placeholders: List<String>
)