package com.project.mukchoice.model.group

data class CreateGroupInvitationRequest(
    val inviteeUserNo: Int?,
    val groupId: Long,
)
