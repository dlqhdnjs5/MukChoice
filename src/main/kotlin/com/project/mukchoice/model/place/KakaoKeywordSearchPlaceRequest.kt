package com.project.mukchoice.model.place

data class KakaoKeywordSearchPlaceRequest(
    val query: String,
    val category_group_code: String = "FD6", // code 음식점
    val x: String? = null,
    val y: String? = null,
    val radius: Int? = null,
    val rect: String? = null,
    val page: Int? = 1,
    val size: Int? = 15,
    val sort: String? = "accuracy"
)
