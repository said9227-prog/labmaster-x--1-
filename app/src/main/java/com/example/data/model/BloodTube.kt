package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_tubes")
data class BloodTube(
    @PrimaryKey val id: String,
    val colorNameAr: String,
    val colorNameEn: String,
    val colorHex: String,
    val orderOfDraw: Int,
    val additiveAr: String,
    val additiveEn: String,
    val specimenType: String,
    val suitableTests: String, // comma-separated or text
    val drawReasons: String,
    val errorsIfWrongTube: String,
    val carryoverContaminationRisk: String,
    val mixingInversions: String,
    val centrifugationSpecs: String,
    val manufacturerNotes: String,
    val imageResName: String = ""
)
