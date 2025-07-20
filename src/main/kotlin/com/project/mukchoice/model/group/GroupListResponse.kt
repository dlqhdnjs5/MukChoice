package com.project.mukchoice.model.group

import java.time.LocalDateTime

data class GroupMember(
    val userNo: Int,
    val isOwner: Boolean,
    val email: String,
    val nickName: String,
    val imgPath: String?
)

data class GroupListResponse(
    val groupId: Long,
    val groupName: String,
    val regTime: LocalDateTime,
    val modTime: LocalDateTime,
    val members: List<GroupMember>,
    val memberCount: Int,
    val placeCount: Int
)
