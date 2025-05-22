package com.example.documenthelper.documents.presentation.filldocument

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.documenthelper.core.Navigation
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsScreen
import com.example.documenthelper.documents.presentation.documentsmain.DocumentsUiState
import java.io.File
import androidx.core.net.toUri
import com.example.documenthelper.documents.domain.utils.DocumentFiller
import com.example.documenthelper.documents.domain.utils.livedata.FilledValuesLiveDataWrapper
import com.example.documenthelper.documents.presentation.preview.DocumentPreviewScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FillDocumentViewModel(
    private val navigation: Navigation,
    private val clickedDocumentLiveDataWrapper: CurrentDocumentLiveDataWrapper,
    private val filledValuesLiveDataWrapper: FilledValuesLiveDataWrapper,
    private val attachmentsLiveDataWrapper: AttachmentsLiveDataWrapper
) : ViewModel() {

    fun getPlaceHolders() : DocumentFillUiState {
        clickedDocumentLiveDataWrapper.liveData().value?.let {
            if (it.placeholders.isNotEmpty()) {
                return DocumentFillUiState.PlaceHoldersExist(it)
            } else {
                return DocumentFillUiState.PlaceHoldersDoesntExist
            }
        }
        return DocumentFillUiState.Error("Ошибка при получении документа")
    }

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

    fun updateCurrentFileFieldValues(filedValues: Map<String, String>) {
        filledValuesLiveDataWrapper.update(filedValues)
    }

    fun updateAttachments(attachments: List<Pair<String, String>>) {
        attachmentsLiveDataWrapper.update(attachments)
    }

    fun documentPreviewScreen() {
        navigation.update(DocumentPreviewScreen)
    }

    fun clickedDocLiveData() = clickedDocumentLiveDataWrapper.liveData()
}
