package com.project.mukchoice.model.wish

import com.project.mukchoice.model.place.PlaceDto
import java.time.LocalDateTime

class WishListResponse(
    val wishList: List<WishDto>,
    val total: Long
)

class WishDto(
    val userNo: Int,
    val placeId: Long,
    val regTime: LocalDateTime?,
    val place: PlaceDto?
)
