package com.notesapp.compressify.domain.useCase

import com.notesapp.compressify.CompressApplication
import com.notesapp.compressify.R
import com.notesapp.compressify.domain.model.CategoryModel
import com.notesapp.compressify.util.getAllAudioFilesSize
import com.notesapp.compressify.util.getAllDocumentSize
import com.notesapp.compressify.util.getAllImageFilesSize
import com.notesapp.compressify.util.getAllVideoFilesSize
import com.notesapp.compressify.util.getOtherFilesSize
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetCategoryStorageUseCase @Inject constructor() : BaseUseCase<BaseUseCase.Parameters, List<CategoryModel>>() {

    override suspend fun launch(parameters: Parameters): List<CategoryModel> {
        val categoryModels = coroutineScope {
           CategoryModel.CategoryType.values().map {
                async {
                    createCategoryModel(it)
                }
            }.awaitAll().run {
                filterNot {
                    it.categoryType == CategoryModel.CategoryType.OTHER
                }.sortedByDescending { it.size } + filter {
                    it.categoryType == CategoryModel.CategoryType.OTHER
                }
           }
        }
        var startingPoint = 0f
        var endingPoint = 0f
        val totalOccupiedSize = categoryModels.sumOf { it.size.toDouble() }
        for (category in categoryModels) {
            val size = (category.size * 100) / totalOccupiedSize
            endingPoint += size.toFloat()
            category.startingPoint = startingPoint
            category.endPoint = endingPoint
            startingPoint = endingPoint
        }
        return categoryModels
    }

    private fun createCategoryModel(categoryType: CategoryModel.CategoryType): CategoryModel {
        return when (categoryType) {
            CategoryModel.CategoryType.VIDEO -> {
                val size = CompressApplication.appContext.getAllVideoFilesSize()
                val icon = R.drawable.ic_category_video
                CategoryModel(
                    icon,
                    categoryType,
                    size
                )
            }
            CategoryModel.CategoryType.AUDIO -> {
                val size = CompressApplication.appContext.getAllAudioFilesSize()
                val icon = R.drawable.ic_category_audio
                CategoryModel(
                    icon,
                    categoryType,
                    size
                )
            }
            CategoryModel.CategoryType.IMAGE -> {
                val size = CompressApplication.appContext.getAllImageFilesSize()
                val icon = R.drawable.ic_category_image
                CategoryModel(
                    icon,
                    categoryType,
                    size
                )
            }
            CategoryModel.CategoryType.DOCUMENT -> {
                val size = CompressApplication.appContext.getAllDocumentSize()
                val icon = R.drawable.ic_category_document
                CategoryModel(
                    icon,
                    categoryType,
                    size
                )
            }
            CategoryModel.CategoryType.OTHER -> {
                val size = CompressApplication.appContext.getOtherFilesSize()
                val icon = R.drawable.ic_category_other
                CategoryModel(
                    icon,
                    categoryType,
                    size
                )
            }
        }
    }
}