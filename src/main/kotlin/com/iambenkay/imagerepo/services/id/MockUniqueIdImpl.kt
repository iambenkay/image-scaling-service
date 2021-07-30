package com.iambenkay.imagerepo.services.id

class MockUniqueIdImpl : UniqueId {
    private var counter = 0

    override fun new(): String {
        return "M${++counter}"
    }
}