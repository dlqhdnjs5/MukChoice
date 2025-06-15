package com.project.mukchoice.model.location

import java.time.LocalDateTime

data class LocationDto(
    val locationNo: Int? = null,
    val userNo: Int,
    val addressName: String,
    val x: Double,
    val y: Double,
    val hcode: String?,
    val bcode: String?,
    val postNo: String?,
    val sigungu: String?,
    val sido: String?,
    val dong: String?,
    val userAddress: String? = null,
    val isSelected: Boolean,
    val regTime: LocalDateTime = LocalDateTime.now(),
    val modTime: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromEntity(entity: LocationEntity): LocationDto {
            return LocationDto(
                locationNo = entity.locationNo,
                userNo = entity.userNo,
                addressName = entity.addressName,
                x = entity.x,
                y = entity.y,
                hcode = entity.hcode,
                bcode = entity.bcode,
                postNo = entity.postNo,
                sigungu = entity.sigungu,
                sido = entity.sido,
                dong = entity.dong,
                userAddress = entity.userAddress,
                regTime = entity.regTime,
                modTime = entity.modTime,
                isSelected = entity.isSelected
            )
        }
    }
}
