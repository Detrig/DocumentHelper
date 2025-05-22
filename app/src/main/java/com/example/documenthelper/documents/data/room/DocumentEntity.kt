package com.example.documenthelper.documents.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val uriString: String, //путь к файлу
    val placeholders: List<String> = emptyList(),
    val dateCreating: String = ""
)