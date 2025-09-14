package com.project.mukchoice.model.group

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "group_invitation")
class GroupInvitationEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "inviter_user_no", nullable = false)
    val inviterUserNo: Int,

    @Column(name = "group_id", nullable = false)
    val groupId: Long,

    @Column(name = "status", nullable = false)
    var status: Boolean,

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, updatable = false)
    val regTime: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "mod_time", nullable = false)
    var modTime: LocalDateTime? = null
) {
    constructor(
        id: String,
        inviterUserNo: Int,
        groupId: Long,
        status: Boolean
    ) : this(
        id = id,
        inviterUserNo = inviterUserNo,
        groupId = groupId,
        status = status,
        regTime = null,
        modTime = null
    )
}
