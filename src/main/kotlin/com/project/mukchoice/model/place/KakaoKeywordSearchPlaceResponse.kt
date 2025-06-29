package com.project.mukchoice.model.place

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoKeywordSearchPlaceResponse(
    val meta: Meta,
    val documents: List<Document>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Meta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean,
    val same_name: SameName?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SameName(
    val region: List<String>,
    val keyword: String,
    val selected_region: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Document(
    val id: String,
    val place_name: String,
    val category_name: String,
    val category_group_code: String?,
    val category_group_name: String?,
    val phone: String?,
    val address_name: String?,
    val road_address_name: String?,
    val x: String,
    val y: String,
    val place_url: String,
    val distance: String?
)
