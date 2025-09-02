package com.project.mukchoice.config

import lombok.Getter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@Configuration
@Getter
@PropertySources(
    PropertySource(
        value = arrayOf("file:/app/config/private.properties"), // Docker 컨테이너 경로
        encoding = "utf-8",
        ignoreResourceNotFound = true
    ),PropertySource(value = arrayOf("/private.properties"), ignoreResourceNotFound = true)
)
class GlobalPropertySource {
    @Value("\${mukchoice.datasource.driverClassName}")
    lateinit var driverClassName: String
    @Value("\${mukchoice.datasource.url}")
    lateinit var url: String
    @Value("\${mukchoice.datasource.username}")
    lateinit var username: String
    @Value("\${mukchoice.datasource.password}")
    lateinit var password: String
    @Value("\${mukchoice.base.url}")
    lateinit var baseUrl: String
    @Value("\${mukchoice.sec.web.token.key}")
    lateinit var secretWebTokenKey: String
    @Value("\${mukchoice.web.auth.header}")
    lateinit var authHeader: String
    @Value("\${mukchoice.web.auth.token}")
    lateinit var authToken: String
}