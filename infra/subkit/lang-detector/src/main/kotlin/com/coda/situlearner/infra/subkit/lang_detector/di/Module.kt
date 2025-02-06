package com.coda.situlearner.infra.subkit.lang_detector.di

import com.coda.situlearner.infra.subkit.lang_detector.LanguageDetector
import com.coda.situlearner.infra.subkit.lang_detector.Lingua
import org.koin.dsl.module

val languageDetectorModule = module {
    factory<LanguageDetector> { Lingua() }
}