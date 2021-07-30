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
    val id: String,
    val description: String?,
    val mime: String,
)

@Repository
interface ImageRepository : CrudRepository<Image, String>

class MockImageRepositoryImpl : ImageRepository, ArrayList<Image>() {
    override fun <S : Image?> save(entity: S): S {
        val image = this.find { it.id == entity?.id }

        if (image != null) {
            throw Exception("Unique key constraint violated")
        }

        this.add(entity!!)
        return entity
    }

    override fun <S : Image?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Optional<Image> {
        if (id == "M200") {
            return Optional.of(Image("M200", null, "image/jpeg"))
        }
        return Optional.ofNullable(this.find { it.id == id })
    }

    override fun existsById(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableIterable<Image> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<String>): MutableIterable<Image> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Image) {
        this.removeIf { it.id == entity.id }
    }

    override fun deleteAll(entities: MutableIterable<Image>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

}