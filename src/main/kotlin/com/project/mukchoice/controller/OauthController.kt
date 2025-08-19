package com.project.mukchoice.controller

import com.project.mukchoice.consts.InvitationType
import com.project.mukchoice.facade.OauthFacade
import com.project.mukchoice.model.user.UserResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth")
class OauthController(
    val oauthFacade: OauthFacade
) {
    /**
     * 먹초이스화면 에서 카카오 로그인 버튼 클릭시 카카오 로그인 화면 노출.
     * 로그인 화면에서 카카오 로그인 후  /login-callback 으로 콜백 요청옴(카카오 developer 설정에서 redirect URI 등록함)
     * 그후 액세스 토큰을 받아 리다이렉트함
     * state: invite-group_{invitationId}, 형태로 넘어옴
     */
    @GetMapping("/login-callback")
    fun kakaoLoginCallback(
        @RequestParam("code") code: String, @RequestParam(value = "state", required = false) state: String?,
        response: HttpServletResponse
    ) {
        val accessToken = oauthFacade.getKakaoAccessToken(code)
        response.sendRedirect("http://localhost:3000/oauth/redirect?accessToken=${accessToken}&state=${state}")
    }

    @GetMapping("/kakao-login")
    fun kakaoLogin(@RequestParam("accessToken") accessToken: String): UserResponse {
        val userResponse = oauthFacade.kakaoLogin(accessToken)
        return userResponse
    }

    @GetMapping("/kakao-login/invitations/{invitationType}")
    fun kakaoLoginWithInvitation(
        @RequestParam("accessToken") accessToken: String,
        @RequestParam("invitationId") invitationId: String,
        @PathVariable("invitationType") invitationType: InvitationType,
    ): UserResponse {
        val userResponse = oauthFacade.kakaoLoginWithInvitation(accessToken, invitationType, invitationId)
        return userResponse
    }

    @GetMapping("/kakao-logout")
    fun kakaoLogout() {
        return oauthFacade.kakaoLogout()
    }
}