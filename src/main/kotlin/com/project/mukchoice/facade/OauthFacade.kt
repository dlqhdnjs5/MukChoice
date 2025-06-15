package com.project.mukchoice.facade

import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import com.project.mukchoice.manager.JwtManager
import com.project.mukchoice.model.oauth.KakaoUserResponse
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.model.user.UserEntity
import com.project.mukchoice.model.user.UserResponse
import com.project.mukchoice.service.OauthService
import com.project.mukchoice.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OauthFacade(
    private val oauthService: OauthService,
    private val userService: UserService,
    private val jwtManager: JwtManager
) {
    fun getKakaoAccessToken(code: String): String {
        val kakaoTokenResponse = oauthService.getKakaoAccessToken(code)
        return kakaoTokenResponse.accessToken
    }

    @Transactional
    fun kakaoLogin(accessToken: String): UserResponse {
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
            jwtToken = jwtManager.generateToken(userEntity.email, userEntity.userNo.toString())
        )
    }
}