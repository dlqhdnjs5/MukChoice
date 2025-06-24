package com.project.mukchoice.service

import com.project.mukchoice.manager.HttpWebClientManager
import com.project.mukchoice.model.location.Document
import com.project.mukchoice.model.location.KakaoAddressCoordResponse
import com.project.mukchoice.model.location.LocationDto
import com.project.mukchoice.model.location.LocationEntity
import com.project.mukchoice.repository.LocationRepository
import com.project.mukchoice.util.ContextHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class LocationService(
    @Value("\${kakao.rest.api.key}") val kakaoRestApiKey: String,
    private val httpWebClientManager: HttpWebClientManager,
    private val locationRepository: LocationRepository
) {
    fun getCoordinateAddress(address: String): Document {
        val url = "https://dapi.kakao.com/v2/local/search/address?query=${address}&page=1&size=10"
        val headers = HttpHeaders().apply {
            add("Authorization", "KakaoAK $kakaoRestApiKey")
        }

        httpWebClientManager.get(url, headers, KakaoAddressCoordResponse::class.java)?.let { response ->
            if (response.documents.isEmpty()) {
                throw IllegalArgumentException("No coordinates found for the location: $address")
            }
            val firstDocument = response.documents.first()
            return firstDocument
        } ?: throw IllegalStateException("Failed to retrieve coordinates for the location: $address")
    }

    fun addLocation(address: String): LocationDto {
        val addressDoc = getCoordinateAddress(address)
        val user = ContextHolder.getUserInfoWithCheck()
        val locationEntity = LocationEntity(
            userNo = user.userNo!!,
            addressName = addressDoc.address_name,
            x = addressDoc.x.toDouble(),
            y = addressDoc.y.toDouble(),
            hcode = addressDoc.address?.h_code,
            bcode = addressDoc.address?.b_code,
            postNo = addressDoc.road_address?.zone_no,
            sigungu = addressDoc.address?.region_2depth_name,
            sido = addressDoc.address?.region_1depth_name,
            dong = addressDoc.address?.region_3depth_name,
            userAddress = addressDoc.address_name,
            isSelected = true
        )

        getLocationsByUserNo(user.userNo).forEach { location ->
            location.isSelected = false
        }

        locationRepository.insertLocation(locationEntity)

        return LocationDto.fromEntity(locationEntity)
    }

    fun getLocationsByUserNo(userNo: Int): List<LocationEntity> {
        return locationRepository.findLocationsByUserNo(userNo)
    }

    fun removeLocation(locationNo: Int, userNo: Int) {
        locationRepository.deleteLocation(locationNo, userNo)
    }

    fun updateLocation(locationEntity: LocationEntity) {
        locationRepository.updateLocation(locationEntity)
    }

    fun getCurrentLocationByUserNo(userNo: Int): LocationEntity {
        val locations = getLocationsByUserNo(userNo)
        return locations.firstOrNull { it.isSelected }
            ?: throw IllegalArgumentException("No selected location found for user")
    }

    fun getLocationByLocationNo(locationNo: Int): LocationEntity? {
        return locationRepository.findLocationByLocationNo(locationNo)
    }
}