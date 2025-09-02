package com.project.mukchoice.model.group

import com.project.mukchoice.model.place.PlaceDto
import java.time.LocalDateTime

data class GroupPlaceInfo(
    val placeId: Long,
    val register: Int,
    val regTime: LocalDateTime,
    val placeName: String? = null,
    val addressName: String? = null,
    val placeUrl: String? = null
)

data class GroupDetailResponse(
    val groupId: Long,
    val groupName: String,
    val regTime: LocalDateTime,
    val modTime: LocalDateTime,
    val members: List<GroupMember>,
    val memberCount: Int,
    val places: List<PlaceDto>,
    val placeCount: Int
)
