package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reference_ranges")
data class ReferenceRange(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: String,
    val testNameAr: String,
    val testNameEn: String,
    val minVal: Double,
    val maxVal: Double,
    val unit: String,
    val specimenType: String,
    val gender: String, // Male, Female, Both
    val ageGroup: String, // Neonate, Child, Adult, Elderly, Pregnancy
    val methodOrEquipment: String,
    val source: String,
    val notes: String
)
