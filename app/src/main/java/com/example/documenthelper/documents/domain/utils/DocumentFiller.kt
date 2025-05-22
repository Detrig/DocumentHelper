package com.example.documenthelper.documents.domain.utils

import android.util.Log
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.io.InputStream

object DocumentFiller {

    fun fillDocument(inputStream: InputStream, fieldValues: Map<String, String>): XWPFDocument {
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
                // Удаляем все runs из параграфа
                while (paragraph.runs.isNotEmpty()) {
                    paragraph.removeRun(0)  // Удаляем первый run
                }
                val run = paragraph.createRun()
                run.setText(paragraphText)  // Добавляем новый текст
                fullText.append(paragraphText).append(" ")  // Объединяем текст в одну строку с пробелом
            }
        }

        // Извлекаем текст из таблиц
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
                    run.setText(cellText)  // Устанавливаем новый текст
                    fullText.append(cellText).append(" ")  // Добавляем текст ячейки в общий текст
                }
            }
        }

        // Логируем полный текст
        Log.d("alz-04", "Full Text after processing: $fullText")


        return document
    }
}