package com.iambenkay.imagerepo.services.storage

import com.iambenkay.imagerepo.utils.ImageSize
import java.awt.image.BufferedImage
import java.io.InputStream

interface StorageService {
    fun save(id: String, t: BufferedImage, size: ImageSize)
    fun retrieve(id: String, size: ImageSize): InputStream
    fun delete(id: String)
}