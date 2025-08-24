package com.project.mukchoice.model.user

import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import com.project.mukchoice.converter.EncryptionConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no", nullable = false)
    val userNo: Int? = null,

    @Column(name = "email", nullable = false, length = 500)
    @Convert(converter = EncryptionConverter::class)
    val email: String,

    @Column(name = "nick_name", nullable = false, length = 100)
    val nickName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false, length = 10)
    val statusCode: UserStatusCode,

    @Enumerated(EnumType.STRING)
    @Column(name = "type_code", nullable = false, length = 20)
    val typeCode: UserTypeCode,

    @Column(name = "img_path", nullable = true, length = 500)
    @Convert(converter = EncryptionConverter::class)
    val imgPath: String?,

    @Column(name = "last_login_time", nullable = true)
    var lastLoginTime: LocalDateTime?,

    @Column(name = "reg_time", nullable = false)
    val regTime: LocalDateTime,

    @Column(name = "mod_time", nullable = false)
    var modTime: LocalDateTime,
) {
    constructor(email: String, nickName: String, statusCode: UserStatusCode, typeCode: UserTypeCode, imgPath: String?, lastLoginTime: LocalDateTime?) : this(
        userNo = null,
        email = email,
        nickName = nickName,
        statusCode = statusCode,
        typeCode = typeCode,
        imgPath = imgPath,
        lastLoginTime = lastLoginTime,
        regTime = LocalDateTime.now(),
        modTime = LocalDateTime.now()
    )
}