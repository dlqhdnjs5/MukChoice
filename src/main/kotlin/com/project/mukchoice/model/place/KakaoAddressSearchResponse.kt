package com.project.mukchoice.model.place

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoAddressSearchResponse(
    val meta: AddressMeta,
    val documents: List<AddressDocument>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddressMeta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddressDocument(
    val address_name: String,
    val y: String, // 위도
    val x: String, // 경도
    val address_type: String, // REGION(지명) 또는 ROAD(도로명) 또는 REGION_ADDR(지번) 또는 ROAD_ADDR(도로명)
    val address: Address?,
    val road_address: RoadAddress?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
    val address_name: String,
    val region_1depth_name: String, // 시도 단위
    val region_2depth_name: String, // 구 단위
    val region_3depth_name: String, // 동 단위
    val region_3depth_h_name: String, // 행정동 명칭
    val h_code: String, // 행정 코드
    val b_code: String, // 법정 코드
    val mountain_yn: String, // 산 여부, Y 또는 N
    val main_address_no: String, // 지번 주번지
    val sub_address_no: String, // 지번 부번지
    val x: String, // 경도
    val y: String // 위도
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoadAddress(
    val address_name: String,
    val region_1depth_name: String, // 시도 단위
    val region_2depth_name: String, // 구 단위
    val region_3depth_name: String, // 동 단위
    val road_name: String, // 도로명
    val underground_yn: String, // 지하 여부, Y 또는 N
    val main_building_no: String, // 건물 본번
    val sub_building_no: String, // 건물 부번
    val building_name: String, // 건물 이름
    val zone_no: String, // 우편번호
    val x: String, // 경도
    val y: String // 위도
)
