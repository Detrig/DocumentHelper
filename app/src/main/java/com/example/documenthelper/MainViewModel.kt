package com.example.documenthelper

import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.documentsmain.DocumentsScreen

class MainViewModel(private val navigation: Navigation) : ViewModel() {

    fun init(firstRun: Boolean) {
        if (firstRun)
            navigation.update(DocumentsScreen)
    }

    fun liveData() = navigation.liveData()
}