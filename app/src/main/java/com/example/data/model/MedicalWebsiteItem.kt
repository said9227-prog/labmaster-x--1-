package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_websites")
data class MedicalWebsiteItem(
    @PrimaryKey val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: String, // Labs, Hematology, Microbiology, Pharmacology, etc.
    val url: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val isFavorite: Boolean = false,
    val isOfficialOrganization: Boolean = true
)
