package com.iambenkay.imagerepo.types

open class ApiResponse<T>(
    open val error: Boolean,
    open val message: String,
    open val data: T?
)

data class ImageDataResponse(
    override val error: Boolean,
    override val message: String,
    override val data: ImageData?
) : ApiResponse<ImageData>(error, message, data)