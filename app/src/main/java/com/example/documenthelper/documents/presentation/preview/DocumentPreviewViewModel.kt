package com.example.documenthelper.documents.presentation.preview

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.DocumentRepository
import com.example.documenthelper.documents.domain.utils.DocumentFiller
import com.example.documenthelper.documents.domain.utils.livedata.FilledValuesLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.AttachmentsLiveDataWrapper
import com.example.documenthelper.documents.presentation.filldocument.CurrentDocumentLiveDataWrapper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DocumentPreviewViewModel(
    private val navigation: Navigation,
    private val clickedDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper,
    private val filledValuesLiveDataWrapper: FilledValuesLiveDataWrapper,
    private val attachmentsLiveDataWrapper: AttachmentsLiveDataWrapper
) : ViewModel() {

    fun getFilledValues() = filledValuesLiveDataWrapper.liveData().value ?: mapOf()
    fun getAttachments() = attachmentsLiveDataWrapper.liveData().value ?: emptyList()

    fun getClickedDocument() = clickedDocumentLiveDataWrapper.liveData().value

    suspend fun generateFilledDocument(context: Context, fieldValue: Map<String,String>): Uri {
        val documentEntity = clickedDocumentLiveDataWrapper.liveData().value
            ?: throw IllegalStateException("Документ не выбран")

        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "filled_${documentEntity.name}"
        ).apply {
            parentFile?.mkdirs()  // Создаем папку, если её нет
        }
        Log.d("alz-04", "filled_${documentEntity.name}")

        context.contentResolver.openInputStream(documentEntity.uriString.toUri())?.use { inputStream ->

            DocumentFiller.fillDocument(inputStream, fieldValue).use { filledDocument ->
                FileOutputStream(outputFile).use { outStream ->
                    filledDocument.write(outStream)
                }
            }
        } ?: throw IOException("Не удалось открыть исходный файл")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile
        )
    }
}