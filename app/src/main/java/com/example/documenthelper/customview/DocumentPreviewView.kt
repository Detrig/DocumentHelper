package com.example.documenthelper.customview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.documenthelper.core.ProvideViewModel
import com.example.documenthelper.documents.presentation.preview.DocumentPreviewViewModel

class DocumentPreviewView : ScrollView, UpdateDocumentPreviewView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var state: DocumentPreviewUiState = DocumentPreviewUiState.Empty

    private val container: LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(16, 16, 16, 16)
    }

    init {
        addView(container)
    }

    override fun update(state: DocumentPreviewUiState) {
        this.state = state
        state.update(this)
    }

    override fun update(
        template: String,
        filledValues: HashMap<String, String>,
        highlight: Boolean
    ) {
        //Log.d("alz-04", "CustomView\nfilledValues: $filledValues, \ntemplate: $template")
        updatePreview(template, filledValues, highlight)
    }

    /**
     * @param filledValue Map<String, String> - ключ, введенное_слово
     */
    private fun updatePreview(
        originalTemplate: String,
        filledValues: Map<String, String>,
        highlightEnabled: Boolean
    ) {
        container.removeAllViews() //??

        val documentView = TextView(context).apply {
            setTextIsSelectable(true)
            textSize = 16f
        }

        val spannable = SpannableStringBuilder(originalTemplate)
        val regex = "\\{(.*?)\\}".toRegex()
        val matches = regex.findAll(originalTemplate)
        var offset = 0

        for (match in matches) {
//            val key = match.groupValues[1]
            val value = filledValues[match.value] ?: match.value
            Log.d("alz-04", "value: $value")
            val start = match.range.first + offset
            val end = match.range.last + 1 + offset

            spannable.replace(start, end, value)

            if (highlightEnabled) {
                spannable.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    start,
                    start + value.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            offset += value.length - (end - start)
        }

        documentView.text = spannable
        container.addView(documentView)

        //Прилоожения
        val attachments = (context as? ProvideViewModel)
            ?.viewModel(DocumentPreviewViewModel::class.java)
            ?.getAttachments()
            ?: return

        attachments.forEachIndexed { index, attachment ->
            container.addView(createAttachmentPage(index, attachment))
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()?.let {
            val savedState = DocumentPreviewViewSavedState(it)
            savedState.save(state)
            return savedState
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is DocumentPreviewViewSavedState) {
            super.onRestoreInstanceState(state.superState) //superState - состояние ScrollView
            update(state.restore())
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun createAttachmentPage(index: Int, attachment: Pair<String, String>): View {
        val pageLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 32, 0, 32)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        // "Приложение один"
        val labelView = TextView(context).apply {
            text = "Приложение ${index + 1}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.START
        }

        // Название по центру
        val titleView = TextView(context).apply {
            text = attachment.first
            textSize = 18f
            setTypeface(null, Typeface.BOLD_ITALIC)
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 8, 0, 8)
        }

        // Текст приложения
        val contentView = TextView(context).apply {
            text = attachment.second
            textSize = 16f
            gravity = Gravity.START
        }

        pageLayout.addView(labelView)
        pageLayout.addView(titleView)
        pageLayout.addView(contentView)

        return pageLayout
    }
}

interface UpdateDocumentPreviewView {
    fun update(state: DocumentPreviewUiState)

    fun update(
        template: String,
        filledValues: HashMap<String, String>,
        highlight: Boolean
    )
}