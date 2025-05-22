package com.example.documenthelper.documents.domain.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.documenthelper.documents.data.room.DocumentEntity
import org.apache.poi.xwpf.usermodel.BodyElementType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import java.io.InputStream

class DocxParser(private val context: Context) {

    fun parseDocxFile(uri: Uri): DocumentEntity? {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val placeholders = parseDocxAndFindPlaceholders(inputStream)
                val fileName = getFileNameFromUri(uri)
                return DocumentEntity(
                    name = fileName,
                    placeholders = placeholders,
                    uriString = uri.toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseDocxAndFindPlaceholders(inputStream: InputStream): List<String> {
        val placeholders = mutableListOf<String>()
        val document = XWPFDocument(inputStream)

        document.paragraphs.forEach { paragraph ->
            Regex("\\{(.*?)\\}").findAll(paragraph.text).forEach { match ->
                placeholders.add(match.value)
            }
        }

        document.tables?.forEach { table ->
            table.rows.forEach { row ->
                row.tableCells.forEach { cell ->
                    cell.paragraphs.forEach { paragraph ->
                        Regex("\\{(.*?)\\}").findAll(paragraph.text).forEach { match ->
                            placeholders.add(match.value)
                        }
                    }
                }
            }
        }
        return placeholders.distinct()
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var name = "unknown"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }


    fun extractTextFromDocument(inputStream: InputStream): String {
        val document = XWPFDocument(inputStream)
        val stringBuilder = StringBuilder()

        for (element in document.bodyElements) {
            when (element.elementType) {
                BodyElementType.PARAGRAPH -> {
                    val paragraph = element as XWPFParagraph
                    stringBuilder.append(paragraph.text).append("\n")
                }
                BodyElementType.TABLE -> {
                    val table = element as XWPFTable
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { paragraph ->
                                stringBuilder.append(paragraph.text).append("\n")
                            }
                        }
                    }
                }
                else -> {

                }
            }
        }

        return stringBuilder.toString()
    }


//    fun convertDocxToHtml(inputStream: InputStream, outputFile: File) {
//        try {
//            val document = XWPFDocument(inputStream)
//            val options = XHTMLOptions.create().apply {
//                isEmbedImages = true // Для вложенных изображений
//            }
//
//            val outputStream = FileOutputStream(outputFile)
//            // Используем XHTMLConverter для конвертации
//            XHTMLConverter.getInstance().convert(document, outputStream, options)
//            outputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}