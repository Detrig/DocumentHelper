package com.example.documenthelper.documents.domain.utils

import android.util.Log
import org.apache.poi.xwpf.usermodel.BreakType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.io.InputStream

object DocumentFiller {

    fun fillDocument(
        inputStream: InputStream,
        fieldValues: Map<String, String>,
        attachments: List<Pair<String, String>>
    ): XWPFDocument {
        val document = XWPFDocument(inputStream)
        var fullText = StringBuilder()

        document.paragraphs.forEach { paragraph ->
            var paragraphText = ""
            paragraph.runs.forEach { run ->
                var text = run.text()
                if (!text.isNullOrEmpty()) {
                    fieldValues.forEach { (placeholder, value) ->
                        if (text.contains(placeholder)) {
                            //Log.d("DocumentFiller", "Found placeholder: $placeholder. Replacing with: $value")
                            text = text.replace(placeholder, value)
                        }
                    }

                    paragraphText += text
                }
            }
            if (paragraphText.isNotEmpty()) {
                while (paragraph.runs.isNotEmpty()) {
                    paragraph.removeRun(0)
                }
                val run = paragraph.createRun()
                run.setText(paragraphText)
                fullText.append(paragraphText)
                    .append(" ")
            }
        }

        // Из таблиц
        document.tables.forEach { table ->
            table.rows.forEach { row ->
                row.tableCells.forEach { cell ->
                    var cellText = cell.text
                    // Log.d("DocumentFiller", "Original cell text: $cellText")

                    fieldValues.forEach { (placeholder, value) ->
                        if (cellText.contains(placeholder)) {
                            // Log.d("DocumentFiller", "Found placeholder: $placeholder. Replacing with: $value")
                            cellText = cellText.replace(placeholder, value)
                        }
                    }


                    //Log.d("DocumentFiller", "Updated cell text: $cellText")

                    val cellParagraph = cell.paragraphs[0]
                    while (cellParagraph.runs.isNotEmpty()) {
                        cellParagraph.removeRun(0) // Удаляем все старые runs
                    }
                    val run = cellParagraph.createRun()
                    run.setText(cellText)
                    fullText.append(cellText).append(" ")
                }
            }
        }


        //Список приложений
        if (attachments.isNotEmpty()) {
            document.createParagraph().apply {
                spacingBefore = 200
                spacingAfter = 100
                alignment = ParagraphAlignment.LEFT
                createRun().apply {
                    isBold = true
                    fontSize = 14
                    setText("Приложения:")
                }
            }

            attachments.forEachIndexed { index, (title, _) ->
                document.createParagraph().apply {
                    alignment = ParagraphAlignment.LEFT
                    createRun().apply {
                        fontSize = 12
                        setText("${index + 1}. $title")
                    }
                }
            }
        }


        attachments.forEachIndexed { index, (title, content) ->
            document.createParagraph().createRun().addBreak(BreakType.PAGE)

            document.createParagraph().apply {
                alignment = ParagraphAlignment.LEFT
                createRun().apply {
                    isBold = true
                    fontSize = 14
                    setText("Приложение ${index + 1}")
                }
            }

            document.createParagraph().apply {
                alignment = ParagraphAlignment.CENTER
                createRun().apply {
                    isBold = true
                    fontSize = 16
                    setText(title)
                }
            }

            document.createParagraph().apply {
                alignment = ParagraphAlignment.BOTH
                spacingBetween = 1.5
                createRun().apply {
                    fontSize = 12
                    setText(content)
                }
            }
        }


        return document
    }

}