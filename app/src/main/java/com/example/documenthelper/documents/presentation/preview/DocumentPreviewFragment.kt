package com.example.documenthelper.documents.presentation.preview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.customview.DocumentPreviewUiState
import com.example.documenthelper.databinding.FragmentDocumentPreviewBinding
import com.example.documenthelper.documents.domain.utils.DocxParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class DocumentPreviewFragment : AbstractFragment<FragmentDocumentPreviewBinding>() {

    private lateinit var viewModel: DocumentPreviewViewModel

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDocumentPreviewBinding =
        FragmentDocumentPreviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(DocumentPreviewViewModel::class.java)
        val docxParser = DocxParser(requireContext())

        initViews(docxParser)

        binding.switchHighlightToggleButton.isChecked = false

        binding.switchHighlightToggleButton.setOnCheckedChangeListener { _, isChecked ->
            val document = viewModel.getClickedDocument()
            document?.let {
                val uri =
                    requireContext().contentResolver.openInputStream(document.uriString.toUri())
                        ?: throw IOException("Не удалось открыть файл")
                val template = docxParser.extractTextFromDocument(uri)

                val state = if (isChecked) {
                    DocumentPreviewUiState.HighlightValuesTrue(
                        template,
                        viewModel.getFilledValues() as HashMap<String, String>
                    )
                } else {
                    DocumentPreviewUiState.HighlightValuesFalse(
                        template,
                        viewModel.getFilledValues() as HashMap<String, String>
                    )
                }

                binding.documentPreviewView.update(state)
            }
        }
    }

    private fun initViews(docxParser: DocxParser){
        val document = viewModel.getClickedDocument()
        val attachments = viewModel.getAttachments()
        document?.let {
            val uri = requireContext().contentResolver.openInputStream(document.uriString.toUri())
                ?: throw IOException("Не удалось открыть файл")
            val template = docxParser.extractTextFromDocument(uri)

            binding.documentPreviewView.update(
                DocumentPreviewUiState.HighlightValuesFalse(
                    template,
                    viewModel.getFilledValues() as HashMap<String, String>
                )
            )
        }

        binding.saveButton.setOnClickListener {
            val fieldValues = viewModel.getFilledValues()
            val attachments = viewModel.getAttachments()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val outputFile = viewModel.generateFilledDocument(requireContext(), fieldValues)
                    withContext(Dispatchers.Main) {
                        showResult(outputFile)
                        Toast.makeText(requireContext(), "Файл успешно создан", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка при создании файла: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.d("alz-04", "Ошибка при создании файла: ${e.message}")
                    }
                }
            }
        }
    }

    private fun showResult(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Нет приложения для просмотра DOCX", Toast.LENGTH_SHORT).show()
        }
    }
}