package com.iambenkay.imagerepo.types

data class ImageData(
    val uri: String,
    val description: String?,
    val thumb: String,
    val thumbX2: String,
    val medium: String,
    val original: String,
)