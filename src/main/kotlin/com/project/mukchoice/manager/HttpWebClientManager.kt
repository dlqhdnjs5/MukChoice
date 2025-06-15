package com.project.mukchoice.manager

import com.project.mukchoice.model.oauth.KakaoTokenRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class HttpWebClientManager(
    val restTemplate: RestTemplate
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(HttpWebClientManager::class.java)
    }

    fun <T> getForObject(url: String, responseType: Class<T>, vararg uriVariables: Any): T? {
        return try {
            restTemplate.getForObject(url, responseType, *uriVariables) as T?
        } catch (exception: Exception) {
            logger.error("Error occurred while making GET request to URL: $url", exception)
            throw exception
        }
    }

    fun <T> postForObject(url: String, request: Any, responseType: Class<T>, vararg uriVariables: Any): T? {
        return try {
            restTemplate.postForObject(url, request, responseType, *uriVariables)
        } catch (exception: Exception) {
            logger.error("Error occurred while making POST request to URL: $url", exception)
            throw exception
        }
    }

    fun <T> postForAccssToken(
        url: String,
        request: KakaoTokenRequest,
        responseType: Class<T>,
        vararg uriVariables: Any
    ): T? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("client_id", request.client_id)
            add("redirect_uri", request.redirect_uri)
            add("code", request.code)
            add("grant_type", request.grant_type)
        }

        val httpEntity = HttpEntity(body, headers)

        return try {
            restTemplate.postForObject(url, httpEntity, responseType, *uriVariables)
        } catch (exception: Exception) {
            logger.error("Error occurred while making POST request to URL: $url", exception)
            throw exception
        }
    }

    fun <T> get(url: String, headers: HttpHeaders?, responseType: Class<T>): T? {
        val entity = HttpEntity<String>(null, headers)
        try {
            return restTemplate.exchange<T>(url, HttpMethod.GET, entity, responseType).body
        } catch (exception: RestClientException) {
            logger.error("Error occurred while making GET request to URL: $url", exception)
            throw exception
        }
    }

    fun <T> post(url: String, httpEntity: HttpEntity<*>?, responseType: Class<T>): T? {
        try {
            return restTemplate.exchange<T>(url, HttpMethod.POST, httpEntity, responseType).body
        } catch (exception: RestClientException) {
            logger.error("Error occurred while making POST request to URL: $url", exception)
            throw exception
        }
    }
}