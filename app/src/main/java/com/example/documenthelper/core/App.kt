package com.example.documenthelper.core

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.documenthelper.documents.data.room.AppDatabase
import com.example.documenthelper.documents.data.sharedprefs.SharedPrefsLastOpenedDocumentDataSource

class App : Application(), ProvideViewModel {

    private lateinit var factory: ViewModelFactory

    companion object {
        lateinit var database: AppDatabase
    }

    private val clear: ClearViewModel = object : ClearViewModel {
        override fun clearViewModel(viewModelClass: Class<out ViewModel>) {
            factory.clearViewModel(viewModelClass)
        }
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "documents-db"
        ).build()
        val documentDao = database.documentDao()
        val lastOpenedDocumentDataSource = SharedPrefsLastOpenedDocumentDataSource(applicationContext)

        val provideViewModel = ProvideViewModel.Base(clear, documentDao, lastOpenedDocumentDataSource)
        factory = ViewModelFactory.Base(provideViewModel)
    }

    override fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T =
        factory.viewModel(viewModelClass)

}