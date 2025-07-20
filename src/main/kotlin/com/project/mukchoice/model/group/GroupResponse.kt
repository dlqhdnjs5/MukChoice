package com.project.mukchoice.model.group

import java.time.LocalDateTime

data class GroupResponse(
    val groupId: Long,
    val groupName: String,
    val regTime: LocalDateTime,
    val modTime: LocalDateTime
)
