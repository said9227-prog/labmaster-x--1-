package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_images")
data class MedicalImageItem(
    @PrimaryKey val id: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val category: String, // Blood Smear, Parasites, Urine Crystals, Equipment, etc.
    val source: String,
    val sourceUrl: String,
    val license: String,
    val keywords: String,
    val localFilePath: String = "",
    val remoteUrl: String = "",
    val isFavorite: Boolean = false,
    val dateAdded: String = ""
)
