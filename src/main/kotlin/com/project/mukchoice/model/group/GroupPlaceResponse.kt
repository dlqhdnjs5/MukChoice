package com.project.mukchoice.model.group

import java.time.LocalDateTime

data class GroupPlaceResponse(
    val groupId: Long,
    val placeId: Long,
    val register: Int,
    val regTime: LocalDateTime
)
