package com.project.mukchoice.facade

import com.project.mukchoice.consts.InvitationType
import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import com.project.mukchoice.manager.JwtManager
import com.project.mukchoice.model.oauth.KakaoUserResponse
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.model.user.UserEntity
import com.project.mukchoice.model.user.UserResponse
import com.project.mukchoice.service.GroupInvitationService
import com.project.mukchoice.service.OauthService
import com.project.mukchoice.service.UserService
import com.project.mukchoice.util.ContextHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OauthFacade(
    private val oauthService: OauthService,
    private val userService: UserService,
    private val gouprInvitationService: GroupInvitationService,
    private val jwtManager: JwtManager
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OauthFacade::class.java)
    }

    fun getKakaoAccessToken(code: String): String {
        val kakaoTokenResponse = oauthService.getKakaoAccessToken(code)

        logger.info("please please please23: $kakaoTokenResponse")
        return kakaoTokenResponse.accessToken
    }

    @Transactional
    fun kakaoLogin(accessToken: String): UserResponse {
        val userEntity = processKakaoLogin(accessToken)
        return createUserResponse(userEntity, accessToken)
    }

    @Transactional
    fun kakaoLoginWithInvitation(accessToken: String, invitationType: InvitationType, invitationId: String): UserResponse {
        val userEntity = processKakaoLogin(accessToken)

        acceptInvitation(
            invitationType = invitationType,
            invitationId = invitationId,
            userNo = userEntity.userNo!!
        )

        return createUserResponse(userEntity, accessToken)
    }

    fun kakaoLogout() {
        val accessToken = ContextHolder.getAccessToken()

        if (accessToken.isNullOrBlank()) {
            logger.warn("No access token found in context for logout")
            return
        }

        try {
            oauthService.kakaoLogout(accessToken)
        } catch (exception: Exception) {
            val user = ContextHolder.getUser()
            logger.error("Kakao logout failed. user: ${user.userNo} error: ${exception.message}", exception)
        } finally {
            ContextHolder.clearUser()
            ContextHolder.clearAccessToken()
        }
    }

    private fun processKakaoLogin(accessToken: String): UserEntity {
        val kakaoUserResponse: KakaoUserResponse = oauthService.requestKakaoUserInfo(accessToken)
            ?: throw IllegalStateException("Kakao user response is null")

        val nickName = kakaoUserResponse.properties?.get("nickname")
        require(!nickName.isNullOrBlank()) {
            "Nickname is required and cannot be blank"
        }
        val kakaoAccount = kakaoUserResponse.kakao_account
        require(kakaoAccount != null) {
            "Kakao account information is required"
        }
        val email = kakaoAccount.email
        require(!email.isNullOrBlank()) {
            "Email is required and cannot be blank"
        }

        val userEntity = userService.getUserByEmail(email)?.apply {
            modTime = LocalDateTime.now()
            lastLoginTime = LocalDateTime.now()
        } ?: UserEntity(
            nickName = nickName,
            email = email,
            statusCode = UserStatusCode.ACTIVE,
            typeCode = UserTypeCode.KAKAO,
            imgPath = kakaoAccount.profile?.profile_image_url,
            lastLoginTime = LocalDateTime.now()
        )

        userService.createUser(userEntity)
        return userEntity
    }

    private fun createUserResponse(userEntity: UserEntity, accessToken: String): UserResponse {
        return UserResponse(
            userDTO = UserDto(
                userNo = userEntity.userNo,
                email = userEntity.email,
                nickName = userEntity.nickName,
                statusCode = userEntity.statusCode,
                typeCode = userEntity.typeCode,
                imgPath = userEntity.imgPath,
                lastLoginTime = userEntity.lastLoginTime,
                regTime = userEntity.regTime,
                modTime = userEntity.modTime,
            ),
            jwtToken = jwtManager.generateToken(userEntity.email, userEntity.userNo.toString(), accessToken)
        )
    }

    private fun acceptInvitation(invitationType: InvitationType, invitationId: String, userNo: Int) {
        when (invitationType) {
            InvitationType.group -> gouprInvitationService.acceptInvitationGroup(invitationId, userNo)
        }
    }
}