package com.coda.situlearner.core.cache.util

import java.io.File

internal fun assureDir(path: String) {
    val file = File(path)
    if (!file.exists()) {
        file.mkdirs()
    }
}