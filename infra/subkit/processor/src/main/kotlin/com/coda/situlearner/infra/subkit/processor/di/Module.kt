package com.coda.situlearner.infra.subkit.processor.di

import com.coda.situlearner.infra.subkit.processor.Processor
import com.coda.situlearner.infra.subkit.processor.ProcessorImpl
import org.koin.dsl.module

val processorModule = module {
    factory<Processor> { ProcessorImpl() }
}