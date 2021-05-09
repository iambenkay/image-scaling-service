package com.iambenkay.imagerepo.services.images

import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.exceptions.InvalidImageUploadedException
import com.iambenkay.imagerepo.models.Image
import com.iambenkay.imagerepo.models.ImageRepository
import com.iambenkay.imagerepo.services.storage.StorageService
import com.iambenkay.imagerepo.types.ImageData
import com.iambenkay.imagerepo.utils.ImageSize
import com.iambenkay.imagerepo.utils.ImageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageService @Autowired constructor(
    val imageRepository: ImageRepository,
    val storageService: StorageService
) {
    /**
     * Creates and stores variants of images
     */
    fun createAndStoreVariants(file: MultipartFile, description: String?): ImageData {
        // implement a mime type check and prevent non-images from going to the next level
        if (!ImageUtils.isImage(file)) throw InvalidImageUploadedException()

        // load file into Image object for processing
        val originalImage = ImageIO.read(file.inputStream)

        // resize originalImage into different sizes
        val thumbImage = ImageUtils.resize(originalImage, ImageSize.THUMB)
        val thumbX2Image = ImageUtils.resize(originalImage, ImageSize.THUMBX2)
        val mediumImage = ImageUtils.resize(originalImage, ImageSize.MEDIUM)

        val imageId = UUID.randomUUID()

        // save media resources
        storageService.save(imageId.toString(), thumbImage, ImageSize.THUMB)
        storageService.save(imageId.toString(), thumbX2Image, ImageSize.THUMBX2)
        storageService.save(imageId.toString(), mediumImage, ImageSize.MEDIUM)
        storageService.save(imageId.toString(), originalImage, ImageSize.ORIGINAL)

        var image = Image(
            id = imageId,
            description = description,
            mime = "image/jpeg",
        )

        // saves image entities to the database
        image = imageRepository.save(image)

        // create image api resource
        return generateImageDataResponse(image)
    }

    /**
     * Retrieves an image resource using its id
     */
    fun retrieveImage(id: String): ImageData {
        val image = imageRepository.findByIdOrNull(UUID.fromString(id)) ?: throw ImageNotFoundException()
        return generateImageDataResponse(image)
    }

    /**
     * Deletes an image resource using its id
     */
    fun deleteImage(id: String) {
        storageService.delete(id)
        val image = imageRepository.findByIdOrNull(UUID.fromString(id)) ?: return
        imageRepository.delete(image)

    }

    /**
     * Retrieves an image by id and size
     */
    fun retrieveImageOfSize(id: String, size: String): File {
        imageRepository.findByIdOrNull(UUID.fromString(id)) ?: throw ImageNotFoundException()

        return storageService.retrieve(id, ImageSize.parse(size))
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