package com.example.documenthelper.documents.documentsmain

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.databinding.FragmentDocumentsBinding
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import java.io.File
import java.io.FileOutputStream

class DocumentsFragment : AbstractFragment<FragmentDocumentsBinding>() {

    private val PICK_DOCX_FILE = 1001
    private lateinit var viewModel : DocumentsViewModel

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDocumentsBinding =
        FragmentDocumentsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(DocumentsViewModel::class.java)
        binding.addDocument.setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword"
            ))
        }
        startActivityForResult(intent, PICK_DOCX_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_DOCX_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.contentResolver?.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                processSelectedDocument(uri)
            }
        }
    }

    private fun processSelectedDocument(uri: Uri) {
        try {
            val tempFile = File(requireContext().cacheDir, "temp_template.docx")
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }

            val placeholders = extractPlaceholders(tempFile)
            if (placeholders.isNotEmpty()) {
                viewModel.fillDocumentScreen(tempFile.absolutePath, placeholders)
            } else {
                Toast.makeText(context, "В документе нет полей для заполнения", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка обработки документа", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun extractPlaceholders(docxFile: File): List<String> {
        return try {
            val wordPackage = WordprocessingMLPackage.load(docxFile)
            val text = wordPackage.mainDocumentPart.content.toString()
            Regex("\\{(.+?)\\}").findAll(text)
                .map { it.groupValues[1] }
                .distinct()
                .toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

}