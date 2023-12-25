package com.notesapp.compressify.domain.model

enum class CompressionRatio (val ratio : Float){
    LOW(0.8f),
    MEDIUM(0.5f),
    HIGH(0.3f)
}