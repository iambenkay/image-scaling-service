package com.iambenkay.imagerepo.models

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "images")
data class Image(
    @Id
    val id: UUID = UUID.randomUUID(),
    val description: String?,
    val mime: String,
)

@Repository
interface ImageRepository : CrudRepository<Image, UUID>