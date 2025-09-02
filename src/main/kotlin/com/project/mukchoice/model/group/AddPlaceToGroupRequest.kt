package com.project.mukchoice.model.group

import com.project.mukchoice.consts.PlaceCategory

data class AddPlaceToGroupRequest(
    val placeId: Long,
    val placeName: String,
    val x: String,
    val y: String,
    val placeCategory: PlaceCategory,
)
