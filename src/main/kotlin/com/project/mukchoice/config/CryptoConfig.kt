package com.project.mukchoice.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CryptoConfig {

    @Value("\${crypto.private-key}")
    private lateinit var privateKey: String

    companion object {
        lateinit var PRIVATE_KEY: String
            private set
    }

    @PostConstruct
    fun init() {
        PRIVATE_KEY = privateKey
    }
}
