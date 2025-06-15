package com.project.mukchoice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class WebClientConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setReadTimeout(30 * 1000)
        requestFactory.setConnectTimeout(30 * 1000)
        return RestTemplate(requestFactory)
    }
}