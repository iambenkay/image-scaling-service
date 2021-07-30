package com.iambenkay.imagerepo.services.storage

import com.iambenkay.imagerepo.exceptions.ImageLoadFailedException
import com.iambenkay.imagerepo.exceptions.ImageNotDeletedException
import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.exceptions.ImageSaveFailedException
import com.iambenkay.imagerepo.utils.ImageSize
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

@Service
class FileStorageServiceImpl : StorageService {
    override fun save(id: String, t: BufferedImage, size: ImageSize) {
        val file = File("uploads/${id}/${size.name.toLowerCase()}")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        try {
            ImageIO.write(t, "jpg", file)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ImageSaveFailedException()
        }
    }

    override fun retrieve(id: String, size: ImageSize): InputStream {
        val file = File("uploads/${id}/${size.name.toLowerCase()}")
        if (file.exists()) {
            try {
                return file.inputStream()
            } catch (e: Exception) {
                throw ImageLoadFailedException()
            }
        } else {
            throw ImageNotFoundException()
        }
    }

    override fun delete(id: String) {
        ImageSize.values().forEach {
            val file = File("uploads/${id}/${it.name.toLowerCase()}")
            if (file.exists()) {
                if (!file.delete()) {
                    throw ImageNotDeletedException()
                }
            }
        }
        val file = File("uploads/${id}")
        file.delete()
    }
}