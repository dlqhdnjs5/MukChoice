package com.project.mukchoice.model.group

import com.project.mukchoice.model.user.UserEntity
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "user_group")
@IdClass(UserGroupId::class)
class UserGroupEntity(
    @Id
    @Column(name = "user_no", nullable = false)
    val userNo: Int,

    @Id
    @Column(name = "group_id", nullable = false)
    val groupId: Long,

    @Column(name = "is_owner", nullable = false)
    var isOwner: Boolean = false,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    val user: UserEntity? = null
)

data class UserGroupId(
    var userNo: Int? = null,
    var groupId: Long? = null
) : Serializable
