package com.notesapp.compressify.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseUseCase <T:BaseUseCase.Parameters, R>{
    open class Parameters
    abstract suspend fun launch(parameters: T) : R

    open fun launchWithFlow(parameters: T) : Flow<R> = flow {
        emit(launch(parameters))
    }
}