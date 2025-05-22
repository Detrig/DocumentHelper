package com.example.documenthelper.documents.domain.utils.livedata

import com.example.documenthelper.core.LiveDataWrapper

interface FilledValuesLiveDataWrapper : LiveDataWrapper.Mutable<Map<String, String>> {
    class Base : FilledValuesLiveDataWrapper, LiveDataWrapper.Abstract<Map<String, String>>()
}