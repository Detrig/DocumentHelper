package com.example.documenthelper.documents.presentation.filldocument

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.documenthelper.R
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.databinding.FragmentFillDocumentBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FillDocumentFragment : AbstractFragment<FragmentFillDocumentBinding>() {

    private lateinit var viewModel: FillDocumentViewModel
    private val inputFields = mutableListOf<AppCompatEditText>()
    private val attachments = mutableListOf<Pair<String, String>>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFillDocumentBinding =
        FragmentFillDocumentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(FillDocumentViewModel::class.java)

        val uiState = viewModel.getPlaceHolders()
        render(uiState)

        setupGenerateButton()
        setupPreviewButton()

        binding.addAttachmentButton.setOnClickListener {
            addAttachmentFields()
        }
    }

    private fun createInputFields(placeholders: List<String>) {
        binding.fieldsContainer.removeAllViews()

        placeholders.forEach { placeholder ->
            val inputLayout = TextInputLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16.dpToPx(), 0, 0)
                }
                hint = placeholder.replace("_", " ").replaceFirstChar { it.uppercase() }
            }

            val editText = AppCompatEditText(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTag(placeholder)
            }

            inputLayout.addView(editText)
            binding.fieldsContainer.addView(inputLayout)

            inputFields.add(editText)
        }
    }

    private fun render(state: DocumentFillUiState) {
        when(state) {
            is DocumentFillUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
            is DocumentFillUiState.PlaceHoldersExist -> {
                createInputFields(state.document.placeholders)
            }
            is DocumentFillUiState.PlaceHoldersDoesntExist -> {
                binding.placeHolderDoesntExistTV.visibility = View.VISIBLE
                binding.titleText.visibility = View.INVISIBLE
                binding.generateButton.visibility = View.INVISIBLE
                binding.addAttachmentButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupPreviewButton() {
        binding.previewButton.setOnClickListener {
            val fieldValues = collectFieldValues()
            Log.d("DH-05", "fieldValues: $fieldValues")
            viewModel.updateCurrentFileFieldValues(fieldValues)
            viewModel.updateAttachments(attachments)
            viewModel.documentPreviewScreen()
        }
    }

    private fun setupGenerateButton() {
        binding.generateButton.setOnClickListener {
            val fieldValues = collectFieldValues()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val outputFile = viewModel.generateFilledDocument(requireContext(), fieldValues, attachments)
                    withContext(Dispatchers.Main) {
                        showResult(outputFile)
                        Toast.makeText(requireContext(), "Файл успешно создан", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка при создании файла: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun collectFieldValues(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (editText in inputFields) {
            val key = editText.tag as String
            val value = editText.text.toString()
            result[key] = value
        }
        return result
    }

    private fun addAttachmentFields() {
        val context = requireContext()

        val attachmentContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16.dpToPx(), 0, 16.dpToPx())
            }
        }

        val titleEditText = EditText(context).apply {
            hint = "Заголовок приложения"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_bg)
        }

        val contentEditText = EditText(context).apply {
            hint = "Текст приложения"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            background = ContextCompat.getDrawable(context, R.drawable.edit_text_bg)
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            gravity = Gravity.TOP or Gravity.START
        }

        val index = attachments.size
        attachments.add(Pair("", "")) // добавляем заранее, знаем индекс

        titleEditText.doAfterTextChanged { text ->
            attachments[index] = Pair(text.toString(), contentEditText.text.toString())
        }

        contentEditText.doAfterTextChanged { text ->
            attachments[index] = Pair(titleEditText.text.toString(), text.toString())
        }

        val removeButton = Button(context).apply {
            text = "Удалить приложение"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
            }
            setOnClickListener {
                binding.fieldsContainer.removeView(attachmentContainer)
                attachments.removeAt(index)
            }
        }

        attachmentContainer.addView(titleEditText)
        attachmentContainer.addView(contentEditText)
        attachmentContainer.addView(removeButton)
        binding.fieldsContainer.addView(attachmentContainer)

        binding.scrollView.post {
            binding.scrollView.smoothScrollTo(0, attachmentContainer.bottom)
        }
    }


    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

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