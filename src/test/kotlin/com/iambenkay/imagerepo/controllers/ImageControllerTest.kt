package com.iambenkay.imagerepo.controllers

import com.iambenkay.imagerepo.exceptions.ImageNotFoundException
import com.iambenkay.imagerepo.services.ImageService
import com.iambenkay.imagerepo.types.ImageData
import com.iambenkay.imagerepo.utils.ImageSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import javax.imageio.ImageIO

@WebMvcTest(ImageController::class)
class ImageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var imageService: ImageService

    private val mockImageData = ImageData(
        uri = "media/M1",
        description = null,
        thumb = "media/M1?size=thumb",
        thumbX2 = "media/M1?size=thumbx2",
        medium = "media/M1?size=medium",
        original = "media/M1?size=original"
    )

    @Test
    fun `image upload fails because image is not provided`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/media")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `image upload succeeds because image is provided`() {
        val file = File("test-image.jpg")
        val image = ImageIO.read(file.inputStream())

        Mockito.`when`(imageService.createAndStoreVariants(image, null)).thenReturn(mockImageData)

        mockMvc.perform(
            MockMvcRequestBuilders
                .multipart("/media")
                .file(MockMultipartFile("image", "test-image.jpg", "image/jpeg", file.readBytes()))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `image retrieve fails because image is not found`() {
        Mockito.`when`(imageService.retrieveImage("M1")).thenThrow(ImageNotFoundException())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `image retrieve succeeds because image is found`() {
        Mockito.`when`(imageService.retrieveImage("M1")).thenReturn(mockImageData)

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `image retrieved by size because image found`() {
        val file = File("test-image.jpg")

        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.ORIGINAL)).thenReturn(file.inputStream())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1?size=original"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().bytes(file.readBytes()))
    }

    @Test
    fun `medium image retrieved`() {
        val file = File("test-image.jpg")

        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.MEDIUM)).thenReturn(file.inputStream())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1?size=medium"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().bytes(file.readBytes()))
    }

    @Test
    fun `thumb image retrieved`() {
        val file = File("test-image.jpg")

        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.THUMB)).thenReturn(file.inputStream())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1?size=thumb"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().bytes(file.readBytes()))
    }

    @Test
    fun `thumbX2 image retrieved`() {
        val file = File("test-image.jpg")

        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.THUMBX2)).thenReturn(file.inputStream())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1?size=thumbx2"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().bytes(file.readBytes()))
    }

    @Test
    fun `image not retrieved by size because image not found`() {
        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.ORIGINAL)).thenThrow(ImageNotFoundException())

        mockMvc.perform(MockMvcRequestBuilders.get("/media/M1?size=original"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `image deleted`() {
        Mockito.`when`(imageService.retrieveImageOfSize("M1", ImageSize.ORIGINAL)).thenThrow(ImageNotFoundException())

        mockMvc.perform(MockMvcRequestBuilders.delete("/media/M1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}