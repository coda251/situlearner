package com.coda.situlearner.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.coda.situlearner.core.datastore.LocalPlayerStateDataSource
import com.coda.situlearner.core.datastore.LocalUserPreferenceDataSource
import com.coda.situlearner.core.datastore.PlayerStateDataSource
import com.coda.situlearner.core.datastore.PlayerStateProto
import com.coda.situlearner.core.datastore.PlayerStateSerializer
import com.coda.situlearner.core.datastore.UserPreferenceDataSource
import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.datastore.UserPreferenceSerializer
import org.koin.dsl.module

val dataStoreModule = module {

    // NOTE: put datastore provider inside datasource constructor (do not inject them as modules)
    single<UserPreferenceDataSource> {
        LocalUserPreferenceDataSource(providePreferenceDataStore(get(), UserPreferenceSerializer()))
    }

    single<PlayerStateDataSource> {
        LocalPlayerStateDataSource(providePlayerStateDataStore(get(), PlayerStateSerializer()))
    }
}

private fun providePreferenceDataStore(
    context: Context,
    serializer: UserPreferenceSerializer,
): DataStore<UserPreferenceProto> = DataStoreFactory.create(serializer = serializer) {
    context.dataStoreFile("user_preference.pb")
}

private fun providePlayerStateDataStore(
    context: Context,
    serializer: PlayerStateSerializer,
): DataStore<PlayerStateProto> = DataStoreFactory.create(serializer = serializer) {
    context.dataStoreFile("player_state.pb")
}