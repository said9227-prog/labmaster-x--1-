package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_tests")
data class LabTest(
    @PrimaryKey val id: String,
    val nameAr: String,
    val nameEn: String,
    val abbreviation: String,
    val synonyms: String, // comma-separated
    val category: String, // Hematology, Chemistry, etc.
    val purposeAr: String,
    val purposeEn: String,
    val scientificPrincipleAr: String,
    val scientificPrincipleEn: String,
    val sampleType: String,
    val tubeColor: String,
    val tubeAdditive: String,
    val sampleVolume: String,
    val collectionMethod: String,
    val transportConditions: String,
    val preservationConditions: String,
    val sampleStability: String,
    val rejectionCriteria: String,
    val procedureSteps: String,
    val requiredReagents: String,
    val usedEquipment: String,
    val referenceRangesSummary: String,
    val highInterpretationAr: String,
    val lowInterpretationAr: String,
    val highInterpretationEn: String,
    val lowInterpretationEn: String,
    val affectingFactors: String,
    val commonErrors: String,
    val sources: String,
    val linkedImages: String, // comma-separated image ids
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
