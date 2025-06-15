package com.project.mukchoice.manager

import com.project.mukchoice.config.GlobalPropertySource
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class JwtManager(
    private val globalPropertySource: GlobalPropertySource
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(JwtManager::class.java)
        private lateinit var KEY: Key
        private const val EXPIRATION_MS = 5000 * 60 * 60 // 5시간, 카카오 access token 만료 시간과  -1 시간.
    }

    @PostConstruct
    protected fun init() {
        KEY = Keys.hmacShaKeyFor(globalPropertySource.secretWebTokenKey.toByteArray())
    }

    fun resolveJwt(request: HttpServletRequest): String? {
        return request.getHeader(globalPropertySource.authHeader)
    }

    fun resolveJwtFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies ?: return null
        return cookies.firstOrNull { it.name == globalPropertySource.authToken }?.value
    }

    fun generateToken(email: String, userNo: String): String {
        val claims = Jwts.claims().setSubject(email)
        claims["userNo"] = userNo
        val instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(instant))
            .setExpiration(Date(Date.from(instant).time + EXPIRATION_MS))
            .signWith(KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(token)
            return true
        }  catch (expiredJwtException: ExpiredJwtException) {
            logger.warn("Expired JWT Token $expiredJwtException")
        } catch (exception : Exception) {
            logger.warn("error occured : ${exception}")
        }

        return false
    }

    fun getClaimSubjectFromJwt(jwtToken: String): String? {
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(jwtToken).body.subject
    }
}