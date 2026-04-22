package com.coda.situlearner.core.model.infra

enum class ImageFileFormat(
    val extension: String,
) {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    WEBP("webp")
}