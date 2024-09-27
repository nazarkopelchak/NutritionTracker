package com.nazarkopelchak.nutritiontracker.data.remote.dto

data class Ingredient(
    val parsed: List<Parsed>?,
    val text: String
)