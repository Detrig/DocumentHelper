package com.example.documenthelper.documents.presentation.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.customview.DocumentPreviewUiState
import com.example.documenthelper.databinding.FragmentDocumentPreviewBinding

class DocumentPreviewFragment : AbstractFragment<FragmentDocumentPreviewBinding>() {

    val template = "Привет, {name}! Сегодня {day}."
    val values = hashMapOf(
        "name" to "Алексей",
        "day" to "четверг"
    )

    private lateinit var viewModel: DocumentPreviewViewModel

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDocumentPreviewBinding =
        FragmentDocumentPreviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as ProvideViewModel).viewModel(DocumentPreviewViewModel::class.java)

        binding.documentPreviewView.update(DocumentPreviewUiState.HighlightValuesTrue(template, values))
    }
}