package com.iambenkay.imagerepo.utils

import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import java.awt.image.BufferedImage

object ImageUtils {
    /**
     * Resizes BufferedImage using provided ImageSize
     */
    fun resize(image: BufferedImage, size: ImageSize): BufferedImage {
        if (image.height <= size.size && image.width <= size.size) {
            // don't bother resizing if image is already smaller than size
            return image
        }

        // generate resize factor by which to resize image bounds
        val factor = size.size.toDouble() / image.width.coerceAtLeast(image.height)

        // width and height of new image
        val width = image.width * factor
        val height = image.height * factor

        // scale image
        val img = image.getScaledInstance(width.toInt(), height.toInt(), Image.SCALE_SMOOTH)

        // the former operation returns a ToolKitImage so we use Graphics2D to write it to a BufferedImage
        val output = BufferedImage(width.toInt(), height.toInt(), image.type)
        val g2d = output.createGraphics()
        g2d.drawImage(img, 0, 0, null)
        g2d.dispose()

        return output
    }

    /**
     * Used to verify the mimetype of the uploaded image
     */
    fun isImage(file: MultipartFile): Boolean {
        // if image mime type does not start with 'image/' then it is not an image
        return Regex("^image/").containsMatchIn(file.contentType!!)
    }
}

enum class ImageSize(val size: Int) {
    THUMB(60), // represents the size of a thumbnail image
    THUMBX2(120), // represents the size of a thumbnail x2 image
    MEDIUM(512), // represents the size of a medium image
    ORIGINAL(Int.MAX_VALUE); // represents the size of the original image

    companion object {
        fun parse(v: String): ImageSize {
            return when (v) {
                "thumb" -> THUMB
                "thumbx2" -> THUMBX2
                "medium" -> MEDIUM
                else -> ORIGINAL
            }
        }
    }
}