package com.iambenkay.imagerepo.services.storage

import com.iambenkay.imagerepo.utils.ImageSize
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File

@Service
interface StorageService {
    fun save(id: String, t: BufferedImage, size: ImageSize)
    fun retrieve(id: String, size: ImageSize): File
    fun delete(id: String)
}