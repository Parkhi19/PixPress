package com.notesapp.compressify.data.entities

import com.notesapp.compressify.domain.model.LibraryModel
import com.notesapp.compressify.domain.model.MediaCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.UUID

class LibraryEntity(
    @PrimaryKey
    val id: String? = null,
    var originalURI: String? = null,
    var compressedURI: String? = null,
    var timeStamp: Long = System.currentTimeMillis(),
    var category: String = MediaCategory.IMAGE.name
) : RealmObject {

    constructor() : this(UUID.randomUUID().toString(), null, null, System.currentTimeMillis())
    constructor(libraryModel: LibraryModel) : this(
        libraryModel.id,
        libraryModel.originalURI?.toString(),
        libraryModel.compressedURI?.toString(),
        libraryModel.timeStamp,
        libraryModel.category.name
    )
}