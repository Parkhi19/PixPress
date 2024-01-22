package com.notesapp.compressify.domain.model

data class CategoryModel(
    val iconId: Int,
    val categoryType: CategoryType,
    val size: Long
) {
    var startingPoint: Float = 0f
    var endPoint: Float = 0f
    val name: String
        get() = categoryType.name
    enum class CategoryType {
        VIDEO,
        AUDIO,
        IMAGE,
        DOCUMENT,
        OTHER
    }
}
