package com.example.documenthelper.documents.presentation.filldocument

import com.example.documenthelper.core.LiveDataWrapper

interface AttachmentsLiveDataWrapper : LiveDataWrapper.Mutable<List<Pair<String, String>>> {
    class Base : AttachmentsLiveDataWrapper, LiveDataWrapper.Abstract<List<Pair<String, String>>>()
}