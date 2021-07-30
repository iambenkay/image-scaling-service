package com.iambenkay.imagerepo.controllers

import com.iambenkay.imagerepo.exceptions.InvalidImageUploadedException
import com.iambenkay.imagerepo.services.ImageService
import com.iambenkay.imagerepo.types.ApiResponse
import com.iambenkay.imagerepo.types.ImageDataResponse
import com.iambenkay.imagerepo.utils.ImageSize
import com.iambenkay.imagerepo.utils.ImageUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.imageio.ImageIO

@RestController
@RequestMapping("media")
class ImageController @Autowired constructor(
    val imageService: ImageService
) {

    @PostMapping
    fun uploadImage(
        @RequestParam("image") file: MultipartFile,
        @RequestParam("description", required = false) desc: String?,
    ): ResponseEntity<ImageDataResponse> {
        if (!ImageUtils.isImage(file)) throw InvalidImageUploadedException()

        val image = ImageIO.read(file.inputStream)

        val data = imageService.createAndStoreVariants(image, desc)

        return ResponseEntity(
            ImageDataResponse(false, "media uploaded successfully", data),
            HttpStatus.CREATED
        )
    }

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
            val dataStream = imageService.retrieveImageOfSize(id, ImageSize.parse(size))

            val headers = setResponseHeadersWithSize(size)

            val media = dataStream.readAllBytes()

            return ResponseEntity(media, headers, HttpStatus.OK)
        }
    }

    private fun setResponseHeadersWithSize(size: String): HttpHeaders {
        val headers = HttpHeaders()

        headers.cacheControl = CacheControl.noCache().headerValue
        headers.contentType = MediaType.parseMediaType("image/jpeg")
        val w = ImageSize.parse(size)
        headers["X-Image-Size"] = w.name
        
        return headers
    }

    @DeleteMapping("{id}")
    fun deleteImage(
        @PathVariable("id") id: String,
    ): ResponseEntity<ApiResponse<Unit>> {

        imageService.deleteImage(id)

        return ResponseEntity.noContent().build()
    }
}