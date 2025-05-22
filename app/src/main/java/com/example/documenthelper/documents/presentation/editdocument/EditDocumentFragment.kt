package com.example.documenthelper.documents.presentation.editdocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.databinding.FragmentEditDocumentBinding
import com.example.documenthelper.documents.data.room.DocumentEntity

class EditDocumentFragment : AbstractFragment<FragmentEditDocumentBinding>(){

    private lateinit var viewModel : EditDocumentViewModel
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditDocumentBinding =
        FragmentEditDocumentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentDocumentLiveData().observe(viewLifecycleOwner) { doc ->
            if (doc.placeholders.isEmpty()) {
                Toast.makeText(requireContext(), "Данный файл не содержит полей для заполнения", Toast.LENGTH_SHORT).show()
                return@observe
            }
            createEditText(doc)
        }
    }

    private fun createEditText(currentDocument: DocumentEntity) {
        val placeholders = currentDocument.placeholders.map { it.trim('{', '}') }
        placeholders.forEach { placeholder ->
            val editText = EditText(requireContext()).apply {
                hint = placeholder
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8.dpToPx(), 0, 8.dpToPx())
                }
            }
            binding.containerEditTexts.addView(editText)

        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}