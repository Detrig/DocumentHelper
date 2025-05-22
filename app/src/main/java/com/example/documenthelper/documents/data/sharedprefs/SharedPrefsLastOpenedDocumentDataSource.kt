package com.example.documenthelper.documents.data.sharedprefs

import android.content.Context
import com.example.documenthelper.documents.domain.LastOpenedDocumentDataSource
import androidx.core.content.edit

class SharedPrefsLastOpenedDocumentDataSource(
    context: Context
) : LastOpenedDocumentDataSource {

    private val prefs = context.getSharedPreferences("opened_doc_prefs", Context.MODE_PRIVATE)

    override fun save(uri: String) {
        prefs.edit { putString("last_uri", uri) }
    }

    override fun get(): String? = prefs.getString("last_uri", null)

    override fun clear() {
        prefs.edit { remove("last_uri") }
    }
}