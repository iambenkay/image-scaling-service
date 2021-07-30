package com.iambenkay.imagerepo.services

import com.iambenkay.imagerepo.exceptions.ImageLoadFailedException
import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.models.MockImageRepositoryImpl
import com.iambenkay.imagerepo.services.id.MockUniqueIdImpl
import com.iambenkay.imagerepo.services.storage.MockFileSystem
import com.iambenkay.imagerepo.services.storage.MockStorageServiceImpl
import com.iambenkay.imagerepo.utils.ImageSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.awt.image.BufferedImage

class ImageServiceTest {

    private val imageRepository = MockImageRepositoryImpl()
    private val mockFileSystem = MockFileSystem()
    private val uniqueId = MockUniqueIdImpl()
    private val storageService = MockStorageServiceImpl(mockFileSystem)
    private val imageService = ImageService(imageRepository, storageService, uniqueId)

    @Test
    fun `test retrieveImage works`() {
        val id = mockFileSystem.getAnyId()

        val media = imageService.retrieveImage(id)

        assert(media.uri == "media/$id")
    }

    @Test
    fun `test retrieveImage fails because of invalid input`() {
        assertThrows<ImageNotFoundException> {
            imageService.retrieveImage("M30")
        }
    }

    @Test
    fun `test retrieveImageOfSize fails because of file system failure`() {
        assertThrows<ImageLoadFailedException> {
            imageService.retrieveImageOfSize("M200", ImageSize.ORIGINAL)
        }
    }

    @Test
    fun `test retrieveImageOfSize fails because of invalid input`() {
        assertThrows<ImageNotFoundException> {
            imageService.retrieveImageOfSize("M30", ImageSize.MEDIUM)
        }

    }

    @Test
    fun `test retrieveImageOfSize works`() {
        val id = mockFileSystem.getAnyId()

        assertDoesNotThrow {
            imageService.retrieveImageOfSize(id, ImageSize.MEDIUM)
        }
    }

    @Test
    fun `test deleteImage works`() {
        val id = mockFileSystem.getAnyId()

        imageService.deleteImage(id)

        assertThrows<ImageNotFoundException> {
            imageService.retrieveImage(id)
        }
    }

    @BeforeEach
    private fun seedFileSystem() {
        for (i in 0 until 3) {
            imageService.createAndStoreVariants(BufferedImage(700, 700, 2), null)
        }
        imageService.createAndStoreVariants(BufferedImage(500, 500, 2), null)
    }

}