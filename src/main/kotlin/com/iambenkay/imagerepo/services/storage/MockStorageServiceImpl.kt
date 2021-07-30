package com.iambenkay.imagerepo.services.storage

import com.iambenkay.imagerepo.exceptions.ImageLoadFailedException
import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.utils.ImageSize
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

class MockStorageServiceImpl(
    private val mockFileSystem: MockFileSystem
) : StorageService {
    override fun save(id: String, t: BufferedImage, size: ImageSize) {
        val file = MockFile(id, size, t)

        mockFileSystem.add(file)
    }

    override fun retrieve(id: String, size: ImageSize): InputStream {
        if (id == "M200") {
            throw ImageLoadFailedException()
        }
        val file = mockFileSystem.stream().filter { it.size == size && it.id == id }
            .findFirst().orElseThrow { throw ImageNotFoundException() }

        val outputStream = ByteArrayOutputStream()

        ImageIO.write(file.data, "jpg", outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        outputStream.close()

        return inputStream
    }

    override fun delete(id: String) {
        mockFileSystem.removeIf { it.id == id }
    }
}

class MockFile(
    val id: String,
    val size: ImageSize,
    val data: BufferedImage,
)

class MockFileSystem : ArrayList<MockFile>() {
    fun getAnyId(): String {

        return find { true }?.id ?: throw Exception("couldn't find file in mockFileSystem")
    }
}