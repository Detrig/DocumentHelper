package com.example.documenthelper.documents.presentation.filldocument

import com.example.documenthelper.core.LiveDataWrapper
import com.example.documenthelper.documents.data.room.DocumentEntity

interface CurrentDocumentLiveDataWrapper : LiveDataWrapper.Mutable<DocumentEntity> {

    class Base : CurrentDocumentLiveDataWrapper, LiveDataWrapper.Abstract<DocumentEntity>()
}