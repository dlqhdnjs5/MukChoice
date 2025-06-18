package com.project.mukchoice.model.place

import com.project.mukchoice.consts.PlaceCategory

data class PlaceDto(
    val id: String,
    val placeName: String,
    val categoryName: String,
    val categoryGroupCode: String?,
    val categoryGroupName: String?,
    val phone: String?,
    val addressName: String?,
    val roadAddressName: String?,
    val x: String,
    val y: String,
    val placeUrl: String,
    var thumbnailUrl: String?,
    val distance: String?,
    val totalCount: Int? = null,
    val isEnd: Boolean? = null,
    var placeCategory: PlaceCategory? = null,
    var isWish: Boolean? = null,
) {
    companion object {
        fun fromDocument(document: Document): PlaceDto {
            return PlaceDto(
                id = document.id,
                placeName = document.place_name,
                categoryName = document.category_name,
                categoryGroupCode = document.category_group_code,
                categoryGroupName = document.category_group_name,
                phone = document.phone,
                addressName = document.address_name,
                roadAddressName = document.road_address_name,
                x = document.x,
                y = document.y,
                placeUrl = document.place_url,
                thumbnailUrl = null,
                distance = document.distance
            )
        }
    }
}

