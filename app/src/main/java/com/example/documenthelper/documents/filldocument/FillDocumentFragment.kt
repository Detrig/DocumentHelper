package com.example.documenthelper.documents.filldocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.documenthelper.core.AbstractFragment
import com.example.documenthelper.databinding.FragmentFillDocumentBinding

class FillDocumentFragment : AbstractFragment<FragmentFillDocumentBinding>() {
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFillDocumentBinding =
        FragmentFillDocumentBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}