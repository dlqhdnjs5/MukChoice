package com.project.mukchoice.service

import com.project.mukchoice.config.GlobalPropertySource
import com.project.mukchoice.manager.HttpWebClientManager
import com.project.mukchoice.model.oauth.KakaoLogoutResponse
import com.project.mukchoice.model.oauth.KakaoTokenRequest
import com.project.mukchoice.model.oauth.KakaoTokenResponse
import com.project.mukchoice.model.oauth.KakaoUserResponse
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    // authorization code 중복 처리 방지를 위한 캐시 (코드와 만료시간을 함께 저장)
    private val processedCodes = ConcurrentHashMap<String, LocalDateTime>()

    // 스케줄된 실행자 (정기적 캐시 정리용)
    private val scheduler = Executors.newScheduledThreadPool(1)

    @PostConstruct
    fun initCacheCleanup() {
        // 1분마다 만료된 캐시 정리
        scheduler.scheduleAtFixedRate({
            try {
                cleanExpiredCodes()
            } catch (e: Exception) {
                logger.error("Error occurred while cleaning expired codes", e)
            }
        }, 1, 1, TimeUnit.MINUTES)

        logger.info("OAuth cache cleanup scheduler initialized")
    }

    @PreDestroy
    fun shutdown() {
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
        }
        logger.info("OAuth service scheduler shutdown completed")
    }

    fun getKakaoAccessToken(code: String): KakaoTokenResponse {
        // 이미 처리된 코드인지 확인
        if (processedCodes.containsKey(code)) {
            logger.warn("Duplicate request detected for authorization code: $code - ignoring")
            // 중복 요청은 무시하고 빈 응답 반환하지 않고 예외 발생
            throw IllegalStateException("Duplicate authorization code request")
        }

        // 처리 시작 전에 코드를 캐시에 추가 (30초 후 만료)
        val expiryTime = LocalDateTime.now().plusSeconds(30)
        processedCodes[code] = expiryTime

        try {
            val kakaoTokenResponse = requestOauthToken(code)
                ?: throw IllegalStateException("Kakao token response is null")

            logger.info("please please please: $kakaoTokenResponse")

            return kakaoTokenResponse
        } catch (e: Exception) {
            // 실패한 경우 캐시에서 제거
            processedCodes.remove(code)
            throw e
        }
    }

    private fun cleanExpiredCodes() {
        val now = LocalDateTime.now()
        val expiredCodes = processedCodes.entries.filter { it.value.isBefore(now) }

        if (expiredCodes.isNotEmpty()) {
            expiredCodes.forEach {
                processedCodes.remove(it.key)
            }
            logger.info("Removed ${expiredCodes.size} expired codes from cache")
        }
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