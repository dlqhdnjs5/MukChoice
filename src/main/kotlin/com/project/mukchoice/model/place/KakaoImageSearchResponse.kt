package com.project.mukchoice.model.place

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoImageSearchResponse(
    val meta: KakaoImageSearchMeta,
    val documents: List<KakaoImageSearchDocument>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoImageSearchMeta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoImageSearchDocument(
    val collection: String,
    val thumbnail_url: String,
    val image_url: String,
    val width: Int,
    val height: Int,
    val display_sitename: String,
    val doc_url: String,
    val datetime: String
)