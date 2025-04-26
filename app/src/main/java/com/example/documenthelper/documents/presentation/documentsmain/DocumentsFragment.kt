package com.example.documenthelper.documents.presentation.documentsmain

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.databinding.FragmentDocumentsBinding
import com.example.documenthelper.documents.data.room.DocumentEntity
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream

class DocumentsFragment : AbstractFragment<FragmentDocumentsBinding>() {

    private lateinit var viewModel : DocumentsViewModel
    private lateinit var adapter : DocumentAdapter

    // Регистрируем контракт для выбора файла
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                parseDocxFile(uri)
            }
        }
    }

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

    private fun initRcView() {
        adapter = DocumentAdapter(object : DocumentAdapter.OnDocumentClickLisnter {
            override fun onClick(document: DocumentEntity) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // Только .docx
        }
        filePickerLauncher.launch(intent)
    }

    private fun parseDocxFile(uri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val placeholders = parseDocxAndFindPlaceholders(inputStream)
                val documentEntity = DocumentEntity(name = "document.docx", placeholders = placeholders, uriString = uri.toString())
                viewModel.insertDocument(documentEntity)
                Log.d("Document", placeholders.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка при чтении файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseDocxAndFindPlaceholders(inputStream: InputStream): List<String> {
        val placeholders = mutableListOf<String>()
        val document = XWPFDocument(inputStream)

        document.paragraphs.forEach { paragraph ->
            Regex("\\{(.*?)\\}").findAll(paragraph.text).forEach { match ->
                placeholders.add(match.value)
            }
        }

        document.tables?.forEach { table ->
            table.rows.forEach { row ->
                row.tableCells.forEach { cell ->
                    cell.paragraphs.forEach { paragraph ->
                        Regex("\\{(.*?)\\}").findAll(paragraph.text).forEach { match ->
                            placeholders.add(match.value)
                        }
                    }
                }
            }
        }

        return placeholders.distinct()
    }

//    private fun showPlaceholders(placeholders: List<String>) {
//        view?.findViewById<TextView>(R.id.tv_placeholders)?.text =
//            if (placeholders.isEmpty()) "Плейсхолдеры не найдены"
//            else placeholders.joinToString("\n")
//    }
}