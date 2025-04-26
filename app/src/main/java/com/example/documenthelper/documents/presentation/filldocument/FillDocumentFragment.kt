package com.example.documenthelper.documents.presentation.filldocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.databinding.FragmentFillDocumentBinding
import kotlinx.coroutines.launch

class FillDocumentFragment : AbstractFragment<FragmentFillDocumentBinding>() {

    private lateinit var viewModel: FillDocumentViewModel
    private val fieldValues = mutableMapOf<String, String>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFillDocumentBinding =
        FragmentFillDocumentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(FillDocumentViewModel::class.java)

//        setupObservers()
//        setupGenerateButton()

        binding.generateButton.setOnClickListener {
            lifecycleScope.launch {
                //val result = viewModel.generateDocument(collectFieldValues())
                //showResult(result)
            }
        }
    }

//    private fun setupObservers() {
//        viewModel.currentDocLiveData().observe(viewLifecycleOwner) { documentData ->
//            documentData?.let { data ->
//                if (data.tempFile.isNotEmpty() && data.placeholders.isNotEmpty()) {
//                    createInputFields(data.placeholders)
//                } else {
//                    showErrorAndExit("Данные документа не загружены")
//                }
//            } ?: showErrorAndExit("Документ не выбран")
//        }
//    }
//
//    private fun createInputFields(placeholders: List<String>) {
//        binding.fieldsContainer.removeAllViews()
//
//        placeholders.forEach { placeholder ->
//            val inputLayout = TextInputLayout(requireContext()).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ).apply {
//                    setMargins(0, 16.dpToPx(), 0, 0)
//                }
//                hint = placeholder.replace("_", " ").replaceFirstChar { it.uppercase() }
//            }
//
//            val editText = AppCompatEditText(requireContext()).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                setTag(placeholder)
//            }
//
//            inputLayout.addView(editText)
//            binding.fieldsContainer.addView(inputLayout)
//        }
//    }
//
//    private fun setupGenerateButton() {
//        binding.generateButton.setOnClickListener {
//            collectFieldValues()
//            generateDocument()
//        }
//    }
//
//    private fun collectFieldValues() {
//        for (i in 0 until binding.fieldsContainer.childCount) {
//            val view = binding.fieldsContainer.getChildAt(i)
//            if (view is TextInputLayout) {
//                val editText = view.getChildAt(0) as? EditText
//                editText?.let {
//                    val placeholder = it.tag as String
//                    fieldValues[placeholder] = it.text.toString()
//                }
//            }
//        }
//    }
//
//    private fun generateDocument() {
//        viewModel.currentDocLiveData().value?.let { data ->
//            if (fieldValues.size == data.placeholders.size) {
//                lifecycleScope.launch(Dispatchers.IO) {
//                    try {
//                        val result = viewModel.generateDocument(fieldValues)
//                        withContext(Dispatchers.Main) {
//                            showResult(result)
//                        }
//                    } catch (e: Exception) {
//                        withContext(Dispatchers.Main) {
//                            showError("Ошибка генерации: ${e.localizedMessage}")
//                        }
//                    }
//                }
//            } else {
//                showError("Заполните все поля")
//            }
//        }
//    }
//
//    private fun showResult(outputFile: File) {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Документ готов")
//            .setMessage("Файл сохранен: ${outputFile.name}")
//            .setPositiveButton("Открыть") { _, _ ->
//                openDocument(outputFile)
//            }
//            .setNegativeButton("Закрыть", null)
//            .show()
//    }
//
//    private fun openDocument(file: File) {
//        val uri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().packageName}.provider",
//            file
//        )
//
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//
//        try {
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            showError("Установите приложение для просмотра DOCX")
//        }
//    }
//
//    private fun showError(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
//    }
//
//    private fun showErrorAndExit(message: String) {
//        showError(message)
//        parentFragmentManager.popBackStack()
//    }
//
//    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}