package com.coda.situlearner.core.network.di

import com.coda.situlearner.core.network.client
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> { client }
}