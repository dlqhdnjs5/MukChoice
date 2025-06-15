package com.project.mukchoice.controller

import com.project.mukchoice.facade.OauthFacade
import com.project.mukchoice.model.user.UserResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth")
class OauthController(
    val oauthFacade: OauthFacade
) {
    @GetMapping("/login-callback")
    fun kakaoLoginCallback(@RequestParam("code") code: String, response: HttpServletResponse) {
        val accessToken = oauthFacade.getKakaoAccessToken(code)
        response.sendRedirect("http://localhost:3000/oauth/redirect?accessToken=${accessToken}")
    }

    @GetMapping("/kakao-login")
    fun kakaoLogin(@RequestParam("accessToken") accessToken: String): UserResponse {
        val userResponse = oauthFacade.kakaoLogin(accessToken)
        return userResponse
    }
}