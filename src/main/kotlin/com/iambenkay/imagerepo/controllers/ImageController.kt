package com.iambenkay.imagerepo.controllers

import com.iambenkay.imagerepo.services.images.ImageService
import com.iambenkay.imagerepo.types.ApiResponse
import com.iambenkay.imagerepo.types.ImageDataResponse
import com.iambenkay.imagerepo.utils.ImageSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * ImageController represents a REST resource for managing Image resources
 */
@RestController
@RequestMapping("media")
class ImageController @Autowired constructor(
    // Image storage service
    val imageService: ImageService
) {
    /**
     * Allows Image upload and stores it to `uploads` directory
     */
    @PostMapping
    fun uploadImage(
        @RequestParam("image") image: MultipartFile, /* Uploaded file */
        @RequestParam("description", required = false) desc: String?,
    ): ResponseEntity<ImageDataResponse> {
        /* Create different versions of images and store */
        val data = imageService.createAndStoreVariants(image, desc)
        return ResponseEntity(
            ImageDataResponse(false, "media uploaded successfully", data),
            HttpStatus.CREATED
        )
    }

    /**
     * Retrieves images uploaded by id and size
     */
    @GetMapping("{id}")
    fun retrieveImage(
        @RequestParam("size", required = false) size: String?,
        @PathVariable("id") id: String,
    ): ResponseEntity<Any> {
        if (size == null) {
            val data = imageService.retrieveImage(id)
            return ResponseEntity.ok(
                ImageDataResponse(false, "media retrieved successfully", data)
            )
        } else {
            val data = imageService.retrieveImageOfSize(id, size)
            val headers = HttpHeaders()
            val inStream = data.inputStream()
            val media = inStream.readAllBytes()
            headers.cacheControl = CacheControl.noCache().headerValue
            headers.contentType = MediaType.parseMediaType("image/jpeg")
            val w = ImageSize.parse(size)
            headers["X-Image-Size"] = w.name

            return ResponseEntity(media, headers, HttpStatus.OK)
        }
    }

    /**
     * Deletes uploaded images
     */
    @DeleteMapping("{id}")
    fun deleteImage(
        @PathVariable("id") id: String,
    ): ResponseEntity<ApiResponse<Unit>> {
        imageService.deleteImage(id)
        return ResponseEntity.noContent().build()
    }
}