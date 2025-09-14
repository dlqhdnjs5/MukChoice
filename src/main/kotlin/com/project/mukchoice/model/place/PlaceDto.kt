package com.project.mukchoice.model.place

import com.project.mukchoice.consts.PlaceCategory

data class PlaceDto(
    val id: String,
    val placeName: String,
    val categoryName: String?,
    val categoryGroupCode: String?,
    val categoryGroupName: String?,
    val phone: String?,
    val addressName: String?,
    val roadAddressName: String?,
    val x: String,
    val y: String,
    var bcode: String? = null,
    var dong: String? = null,
    val placeUrl: String,
    var thumbnailUrl: String?,
    var distance: String?,
    val totalCount: Int? = null,
    val isEnd: Boolean? = null,
    var placeCategory: PlaceCategory? = null,
    var isWish: Boolean? = null,
    var wishCount: Int? = null, // 총 위시 개수 필드 추가
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
                distance = document.distance,
                placeCategory = extractCategoryFromGroupName(document.category_name),
            )
        }

        /**
         * category_group_name에서 두 번째 카테고리를 추출하여 PlaceCategory enum에 매핑
         * 예: "음식점 > 분식 > 떡볶이" -> SNACK_FOOD
         */
        private fun extractCategoryFromGroupName(categoryGroupName: String?): PlaceCategory {
            if (categoryGroupName.isNullOrBlank()) return PlaceCategory.ETC

            val categories = categoryGroupName.split(" > ")
            if (categories.size < 2) return PlaceCategory.ETC

            val secondCategory = categories[1].trim()

            return PlaceCategory.entries
                .filter { it != PlaceCategory.ALL }
                .find { it.displayName == secondCategory }
                ?: PlaceCategory.ETC
        }

        fun fromEntity(entity: PlaceEntity): PlaceDto {
            return PlaceDto(
                id = entity.id.toString(),
                placeName = entity.placeName,
                categoryName = null,
                categoryGroupCode = null,
                categoryGroupName = null,
                phone = entity.phone,
                addressName = entity.addressName,
                roadAddressName = entity.roadAddressName,
                x = entity.x.toString(),
                y = entity.y.toString(),
                placeUrl = entity.placeUrl,
                thumbnailUrl = null,
                distance = null,
                totalCount = null,
                isEnd = null,
                placeCategory = entity.placeCategory,
                isWish = null,
                wishCount = null // 위시 개수 초기화
            )
        }
    }
}
