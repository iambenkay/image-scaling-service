package com.iambenkay.imagerepo

import com.iambenkay.imagerepo.types.ImageData
import com.iambenkay.imagerepo.types.ImageDataResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.FileSystemResource
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.io.File


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImagerepoApplicationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun imageUploadFailsBecauseImageIsNotProvided() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()

        val entity = HttpEntity(body, headers)
        val resp = this.restTemplate.postForEntity("/media", entity, ImageDataResponse::class.java)
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun imageUploadSucceededBecauseImageIsProvided(): ImageData {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val file = File("test-image.jpg")

        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body["image"] = FileSystemResource(file)

        val entity = HttpEntity(body, headers)
        val resp = this.restTemplate.postForEntity("/media", entity, ImageDataResponse::class.java)
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.CREATED)
        return resp.body!!.data!!
    }

    @Test
    fun imageRetrieveFailedBecauseImageNotFound() {
        val resp =
            this.restTemplate.getForEntity("/media/5e029146-b115-11eb-8529-0242ac130003", ImageDataResponse::class.java)
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun imageRetrievedBecauseImageFound() {
        val image = this.imageUploadSucceededBecauseImageIsProvided()
        val resp = this.restTemplate.getForEntity("/${image.uri}", ImageDataResponse::class.java)
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun imageRetrievedBySizeBecauseImageFound() {
        val image = this.imageUploadSucceededBecauseImageIsProvided()
        val resp = this.restTemplate.getForEntity("/${image.medium}", ByteArray::class.java)
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
        val h = resp.headers["X-Image-Size"]
        Assertions.assertThat(h).isNotEqualTo(null)
        Assertions.assertThat(h?.get(0)).isEqualTo("MEDIUM")
    }

    @Test
    fun imageDeletedBecauseImageFound() {
        val image = this.imageUploadSucceededBecauseImageIsProvided()
        val resp = this.restTemplate.exchange(
            "/${image.uri}",
            HttpMethod.DELETE,
            HttpEntity(null, HttpHeaders()),
            Unit::class.java
        )
        Assertions.assertThat(resp.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    companion object {
        @BeforeAll
        fun clearUploadsDirectory(){
            deleteFolder(File("uploads"))
        }
        private fun deleteFolder(file: File) {
            for (subFile in file.listFiles()!!) {
                if (subFile.isDirectory) {
                    deleteFolder(subFile)
                } else {
                    subFile.delete()
                }
            }
            file.delete()
        }
    }
}
