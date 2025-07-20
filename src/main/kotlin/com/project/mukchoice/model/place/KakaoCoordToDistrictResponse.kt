package com.project.mukchoice.model.place

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoCoordToDistrictResponse(
    val meta: CoordToDistrictMeta,
    val documents: List<DistrictDocument>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoordToDistrictMeta(
    val total_count: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DistrictDocument(
    val region_type: String, // H(행정동) 또는 B(법정동)
    val code: String, // 행정 코드
    val address_name: String, // 전체 지역 명칭
    val region_1depth_name: String, // 지역 1Depth, 시도 단위
    val region_2depth_name: String, // 지역 2Depth, 구 단위
    val region_3depth_name: String, // 지역 3Depth, 동 단위
    val region_4depth_name: String, // 지역 4Depth
    val x: Double, // X 좌표값, 경위도인 경우 경도(longitude)
    val y: Double // Y 좌표값, 경위도인 경우 위도(latitude)
)
