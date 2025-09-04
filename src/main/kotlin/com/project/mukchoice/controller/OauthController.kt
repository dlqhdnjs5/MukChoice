package com.project.mukchoice.controller

import com.project.mukchoice.config.GlobalPropertySource
import com.project.mukchoice.consts.InvitationType
import com.project.mukchoice.facade.OauthFacade
import com.project.mukchoice.model.user.UserResponse
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth")
class OauthController(
    val oauthFacade: OauthFacade,
    val globalPropertySource: GlobalPropertySource
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OauthController::class.java)
    }

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
        logger.info("Kakao login callback received - code: $code, state: $state")

        try {
            val accessToken = oauthFacade.getKakaoAccessToken(code)
            logger.info("Successfully got access token, redirecting to frontend...")

            val redirectUrl = "${globalPropertySource.frontendUrl}/kakao-oauth/redirect?accessToken=${accessToken}&state=${state}"
            logger.info("Redirect URL: $redirectUrl")

            response.sendRedirect(redirectUrl)
            logger.info("Redirect response sent successfully")
        } catch (e: IllegalStateException) {
            if (e.message?.contains("Duplicate authorization code request") == true) {
                logger.info("Duplicate request ignored, no response sent")
                // 중복 요청은 아무것도 하지 않음 (이미 첫 번째 요청에서 리다이렉트 완료)
                return
            }
            logger.error("Error in kakaoLoginCallback", e)
            throw e
        } catch (e: Exception) {
            logger.error("Error in kakaoLoginCallback", e)
            throw e
        }
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