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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.databinding.FragmentDocumentsBinding
import com.example.documenthelper.documents.data.room.DocumentEntity
import com.example.documenthelper.documents.domain.utils.DocxParser
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream

class DocumentsFragment : AbstractFragment<FragmentDocumentsBinding>() {

    private lateinit var viewModel : DocumentsViewModel
    private lateinit var adapter : DocumentAdapter
    private lateinit var docxParser: DocxParser

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.saveDocUri(uri.toString())
                docxParser.parseDocxFile(uri)?.let {
                    viewModel.saveDocEntity(it)
                }
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

        binding.docsRcView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel = (activity as ProvideViewModel).viewModel(DocumentsViewModel::class.java)
        docxParser = DocxParser(requireContext())

        initRcView()
        viewModel.loadDocuments()

        binding.addDocument.setOnClickListener {
            openFilePicker()
        }
        binding.addDocumentMainButton.setOnClickListener {
            openFilePicker()
        }

        viewModel.documentLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun initRcView() {
        adapter = DocumentAdapter(object : DocumentAdapter.OnDocumentClickLisnter {
            override fun onClick(document: DocumentEntity) {
                viewModel.fillDocumentScreen(document)
                //Log.d("alz-04", "clicked document: $document")
            }
        })
        binding.docsRcView.adapter = adapter
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // Только .docx
        }
        filePickerLauncher.launch(intent)
    }

//    private fun showPlaceholders(placeholders: List<String>) {
//        view?.findViewById<TextView>(R.id.tv_placeholders)?.text =
//            if (placeholders.isEmpty()) "Плейсхолдеры не найдены"
//            else placeholders.joinToString("\n")
//    }

    private fun render(state: DocumentsUiState) {
        when (state) {
            is DocumentsUiState.Loading -> showOnly(binding.progressBar)
            is DocumentsUiState.Error -> {
                showOnly(binding.docsRcView, binding.addDocument, binding.addDocumentMainButton)
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
            is DocumentsUiState.Success ->  {
                showOnly(binding.docsRcView, binding.addDocument)
                Log.d("DH-01", "Success:  ${state.documentList}")
                adapter.update(ArrayList(state.documentList))
            }
            is DocumentsUiState.EmptyDocumentList -> showOnly(binding.docsRcView, binding.addDocument, binding.addDocumentMainButton)
        }
    }

    private fun showOnly(vararg viewsToShow: View) {
        val allViews = listOf(
            binding.progressBar,
            binding.docsRcView,
            binding.addDocumentMainButton,
            binding.addDocument,
        )
        allViews.forEach { it.isVisible = it in viewsToShow }
    }
}