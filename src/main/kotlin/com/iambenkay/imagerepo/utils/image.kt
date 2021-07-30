package com.iambenkay.imagerepo.utils

import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import java.awt.image.BufferedImage

object ImageUtils {

    fun resize(image: BufferedImage, size: ImageSize): BufferedImage {
        if (image within size) {
            return image
        }

        return image.scaleTo(size)
    }

    fun isImage(file: MultipartFile): Boolean {
        return Regex("^image/").containsMatchIn(file.contentType!!)
    }
}

enum class ImageSize(val size: Int) {
    THUMB(60),
    THUMBX2(120),
    MEDIUM(512),
    ORIGINAL(Int.MAX_VALUE);

    companion object {
        fun parse(v: String): ImageSize {
            return when (v.toLowerCase()) {
                "thumb" -> THUMB
                "thumbx2" -> THUMBX2
                "medium" -> MEDIUM
                else -> ORIGINAL
            }
        }
    }
}

private infix fun BufferedImage.within(size: ImageSize): Boolean =
    this.height <= size.size && this.width <= size.size

private fun BufferedImage.widthFrom(size: ImageSize): Double {
    val factor = size.size.toDouble() / this.width.coerceAtLeast(this.height)

    return this.width * factor
}

private fun BufferedImage.heightFrom(size: ImageSize): Double {
    val factor = size.size.toDouble() / this.width.coerceAtLeast(this.height)

    return this.height * factor
}

private fun BufferedImage.scaleTo(size: ImageSize): BufferedImage {

    val width = this.widthFrom(size)
    val height = this.heightFrom(size)

    val intermediateImage = this.getScaledInstance(width.toInt(), height.toInt(), Image.SCALE_SMOOTH)

    val output = BufferedImage(width.toInt(), height.toInt(), this.type)

    val g2d = output.createGraphics()
    g2d.drawImage(intermediateImage, 0, 0, null)
    g2d.dispose()

    return output
}