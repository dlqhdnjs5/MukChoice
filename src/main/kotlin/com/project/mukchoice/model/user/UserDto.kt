package com.project.mukchoice.model.user

import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import java.time.LocalDateTime

data class UserDto (
    val userNo: Int?,
    val email: String,
    val nickName: String,
    val statusCode: UserStatusCode,
    val typeCode: UserTypeCode,
    val imgPath: String?,
    val lastLoginTime: LocalDateTime?,
    val regTime: LocalDateTime,
    val modTime: LocalDateTime
)
