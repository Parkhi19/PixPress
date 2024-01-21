package com.notesapp.compressify.domain.model

data class CategoryModel(
    val iconId: Int,
    val categoryName: String,
    val size: Long
) {
    var startingPoint: Float = 0f
    var endPoint: Float = 0f
}