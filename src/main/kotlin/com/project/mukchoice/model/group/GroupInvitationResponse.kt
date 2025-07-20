package com.project.mukchoice.model.group

import java.time.LocalDateTime

data class GroupInvitationResponse(
    val id: String,
    val inviterUserNo: Int,
    val inviteeUserNo: Int?,
    val groupId: Long,
    val status: Boolean,
    val regTime: LocalDateTime?,
    val modTime: LocalDateTime?
)
