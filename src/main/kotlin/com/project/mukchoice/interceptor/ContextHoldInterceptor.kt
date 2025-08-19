package com.project.mukchoice.interceptor

import com.project.mukchoice.manager.JwtManager
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.service.UserService
import com.project.mukchoice.util.ContextHolder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class ContextHoldInterceptor(
    private val userService: UserService,
    private val jwtManager: JwtManager,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val token = jwtManager.resolveJwtFromCookie(request)

        if (token == null || !jwtManager.validateToken(token)) {
            ContextHolder.clearUser()
            ContextHolder.clearAccessToken()
            return false
        }

        val email = jwtManager.getClaimSubjectFromJwt(token)
        val accessToken = jwtManager.getAccessTokenFromJwt(token)

        if (email != null && accessToken != null) {
            val userEntity = userService.getUserByEmail(email)

            if (userEntity != null) {
                val userDTO = UserDto(
                    userNo = userEntity.userNo,
                    email = userEntity.email,
                    nickName = userEntity.nickName,
                    statusCode = userEntity.statusCode,
                    typeCode = userEntity.typeCode,
                    imgPath = userEntity.imgPath,
                    lastLoginTime = userEntity.lastLoginTime,
                    regTime = userEntity.regTime,
                    modTime = userEntity.modTime
                )
                ContextHolder.putUser(userDTO)
                ContextHolder.putAccessToken(accessToken)
            }
        }

        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        exception: Exception?
    ) {
        ContextHolder.clearUser()
        super.afterCompletion(request, response, handler, exception)
    }

}