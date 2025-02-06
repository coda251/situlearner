package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * Refer to https://github.com/android/nowinandroid
 */
class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {
    override val data = MutableStateFlow(initialValue)
    override suspend fun updateData(
        transform: suspend (it: T) -> T,
    ) = data.updateAndGet { transform(it) }
}