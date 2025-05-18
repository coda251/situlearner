package com.coda.situlearner.infra.explorer.local

import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import kotlinx.coroutines.flow.Flow

interface LocalExplorer {

    fun getSourceCollections(dir: String): Flow<List<SourceCollection>>

    fun getSourceCollectionWithFiles(dir: String): Flow<SourceCollectionWithFiles>
}