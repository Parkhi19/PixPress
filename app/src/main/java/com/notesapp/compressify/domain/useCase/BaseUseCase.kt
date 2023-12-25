package com.notesapp.compressify.domain.useCase

abstract class BaseUseCase <T:BaseUseCase.Parameters>{
    open class Parameters
    abstract suspend fun launch(parameters: T)
}