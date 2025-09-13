package com.project.mukchoice.service

import com.project.mukchoice.config.GlobalPropertySource
import com.project.mukchoice.manager.HttpWebClientManager
import com.project.mukchoice.model.oauth.KakaoLogoutResponse
import com.project.mukchoice.model.oauth.KakaoTokenRequest
import com.project.mukchoice.model.oauth.KakaoTokenResponse
import com.project.mukchoice.model.oauth.KakaoUserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Service
class OauthService(
    @Value("\${kakao.rest.api.key}") val kakaoRestApiKey: String,
    @Value("\${kakao.rest.api.accesstoken.url}") val kakaoAccessTokenUrl: String,
    @Value("\${kakao.rest.api.user.url}") val kakaoUserUrl: String,
    @Value("\${kakao.rest.api.callback.url}") val kakaoCallbackUrl: String,
    @Value("\${kakao.rest.api.logout.url}") val kakaoLogoutUrl: String,
    val httpWebClientManager: HttpWebClientManager,
    val globalPropertySource: GlobalPropertySource
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OauthService::class.java)
    }

    fun getKakaoAccessToken(code: String): KakaoTokenResponse {
        val kakaoTokenResponse = requestOauthToken(code)
            ?: throw IllegalStateException("Kakao token response is null")

        return kakaoTokenResponse
    }

    fun requestKakaoUserInfo(accessToken: String): KakaoUserResponse? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            add("Authorization", "Bearer $accessToken")
        }
        return httpWebClientManager.get(url = kakaoUserUrl, headers = headers, responseType = KakaoUserResponse::class.java)
    }

    private fun requestOauthToken(code: String): KakaoTokenResponse? {
        logger.info("Requesting OAuth token with code: $code")
        logger.info("Using redirect_uri: ${globalPropertySource.baseUrl}/${kakaoCallbackUrl}")

        KakaoTokenRequest(
            client_id = kakaoRestApiKey,
            redirect_uri = "${globalPropertySource.baseUrl}/${kakaoCallbackUrl}",
            code = code
        ).let { request ->
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

            val kakaoTokenResponse = httpWebClientManager.post(
                url = kakaoAccessTokenUrl,
                responseType = KakaoTokenResponse::class.java,
                httpEntity = httpEntity
            )

            logger.info("Kakao OAuth Token Response: $kakaoTokenResponse")

            return kakaoTokenResponse
        }
    }

    fun kakaoLogout(accessToken: String) {
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $accessToken")
        }

        val httpEntity = HttpEntity(null, headers)

        val logoutResponse = httpWebClientManager.post(
            url = kakaoLogoutUrl,
            responseType = KakaoLogoutResponse::class.java,
            httpEntity = httpEntity
        )

        logger.info("Kakao Logout Response: $logoutResponse")
    }

}