package com.iambenkay.imagerepo.services

import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.models.Image
import com.iambenkay.imagerepo.models.ImageRepository
import com.iambenkay.imagerepo.services.id.UniqueId
import com.iambenkay.imagerepo.services.storage.StorageService
import com.iambenkay.imagerepo.types.ImageData
import com.iambenkay.imagerepo.utils.ImageSize
import com.iambenkay.imagerepo.utils.ImageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.InputStream
import kotlin.jvm.Throws

@Service
class ImageService @Autowired constructor(
    private val imageRepository: ImageRepository,
    private val storageService: StorageService,
    private val uniqueId: UniqueId,
) {
    /**
     * Creates and stores variants of images
     */
    fun createAndStoreVariants(originalImage: BufferedImage, description: String?): ImageData {
        val thumbImage = ImageUtils.resize(originalImage, ImageSize.THUMB)
        val thumbX2Image = ImageUtils.resize(originalImage, ImageSize.THUMBX2)
        val mediumImage = ImageUtils.resize(originalImage, ImageSize.MEDIUM)

        val imageId = uniqueId.new()

        storageService.save(imageId, thumbImage, ImageSize.THUMB)
        storageService.save(imageId, thumbX2Image, ImageSize.THUMBX2)
        storageService.save(imageId, mediumImage, ImageSize.MEDIUM)
        storageService.save(imageId, originalImage, ImageSize.ORIGINAL)

        var image = Image(
            id = imageId,
            description = description,
            mime = "image/jpeg",
        )

        image = imageRepository.save(image)

        return generateImageDataResponse(image)
    }

    /**
     * Retrieves an image resource using its id
     */
    @Throws(ImageNotFoundException::class)
    fun retrieveImage(id: String): ImageData {
        val image = imageRepository.findByIdOrNull(id) ?: throw ImageNotFoundException()
        return generateImageDataResponse(image)
    }

    /**
     * Deletes an image resource using its id
     */
    fun deleteImage(id: String) {
        storageService.delete(id)
        val image = imageRepository.findByIdOrNull(id) ?: return
        imageRepository.delete(image)

    }

    /**
     * Retrieves an image by id and size
     */
    @Throws(ImageNotFoundException::class)
    fun retrieveImageOfSize(id: String, size: ImageSize): InputStream {
        imageRepository.findByIdOrNull(id) ?: throw ImageNotFoundException()

        return storageService.retrieve(id, size)
    }

    /**
     * Used to generate image resource with varying size URIs
     */
    private fun generateImageDataResponse(image: Image): ImageData {
        val base = "media/${image.id}?size="
        return ImageData(
            uri = "media/${image.id}",
            description = image.description,
            thumb = "${base}thumb",
            thumbX2 = "${base}thumbx2",
            medium = "${base}medium",
            original = "${base}original"
        )
    }
}