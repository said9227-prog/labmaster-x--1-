package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_query_history")
data class AiQueryRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val responseMarkdown: String,
    val sourcesJson: String, // list of citations
    val timestamp: Long = System.currentTimeMillis()
)

data class CategoryInfo(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val iconName: String,
    val count: Int = 0
)
