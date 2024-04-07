package com.notesapp.compressify.domain.repository

import android.net.Uri
import com.notesapp.compressify.data.entities.LibraryEntity
import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.model.LibraryModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor() : LibraryRepository {

    private val realm: Realm by lazy {
        val config = RealmConfiguration.create(schema = setOf(LibraryEntity::class))
        Realm.open(config)
    }

    override fun getLibraryItems(): Flow<List<LibraryModel>> {
        return realm.query(LibraryEntity::class).asFlow().map { libraryEntities ->
            libraryEntities.list.map { LibraryModel(it) }
        }
    }

    override suspend fun addLibraryItems(libraryModels: List<LibraryModel>) {
        realm.write {
            libraryModels.forEach { libraryModel ->
                if (libraryModel.id == null) libraryModel.id = UUID.randomUUID().toString()
                copyToRealm(LibraryEntity(libraryModel))
            }
        }
    }

//    override suspend fun deleteLibraryItems(ids: List<String>) {
//       realm.write {
//           val toDelete = realm.query(LibraryEntity::class, "id in $0", ids).find()
//           delete(toDelete)
//       }
//    }

    override suspend fun deleteLibraryItems(uris: List<Uri>) {
        realm.write {
            val uriStrings = uris.map {
                it.toString()
            }
            val toDelete = realm.query(LibraryEntity::class, "originalURI in $0", uriStrings).find()
            delete(toDelete)
        }
    }
}