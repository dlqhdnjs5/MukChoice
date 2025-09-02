package com.project.mukchoice.model.place

import com.project.mukchoice.consts.PlaceCategory

data class AddWishListRequest(
    val placeId: String,
    val placeName: String,
    val placeCategory: PlaceCategory,
    val x: String,
    val y: String,
    val isWish: Boolean
)