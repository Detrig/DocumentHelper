package com.example.documenthelper.customview

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.TextView

class DocumentPreviewView : ScrollView, UpdateDocumentPreviewView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var state: DocumentPreviewUiState = DocumentPreviewUiState.Empty

    private val textView: TextView = TextView(context).apply {
        setTextIsSelectable(true)
        textSize = 16f
        setPadding(16, 16, 16, 16)
    }

    init {
        addView(textView)
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
        val spannable = SpannableStringBuilder(originalTemplate)
        val regex = "\\{(.*?)\\}".toRegex()
        val matches = regex.findAll(originalTemplate)
        var offset = 0

        for (match in matches) {
            val key = match.groupValues[1]
            val value = filledValues[key] ?: continue

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

        textView.text = spannable
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

}

interface UpdateDocumentPreviewView {
    fun update(state: DocumentPreviewUiState)

    fun update(
        template: String,
        filledValues: HashMap<String, String>,
        highlight: Boolean
    )
}