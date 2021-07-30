package com.iambenkay.imagerepo.services.id

import org.springframework.stereotype.Component
import java.util.*

@Component
class UUIDImpl : UniqueId {
    override fun new(): String {
        return UUID.randomUUID().toString()
    }
}