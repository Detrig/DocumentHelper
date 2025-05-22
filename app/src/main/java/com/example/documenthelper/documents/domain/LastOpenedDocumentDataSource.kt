package com.example.documenthelper.documents.domain

interface LastOpenedDocumentDataSource {
    fun save(uri: String)
    fun get(): String?
    fun clear()
}