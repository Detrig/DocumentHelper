package com.example.documenthelper.documents.filldocument

import com.example.documenthelper.core.LiveDataWrapper

interface CurrentDocumentLiveDataWrapper : LiveDataWrapper.Mutable<Document> {

    class Base : CurrentDocumentLiveDataWrapper, LiveDataWrapper.Abstract<Document>()
}