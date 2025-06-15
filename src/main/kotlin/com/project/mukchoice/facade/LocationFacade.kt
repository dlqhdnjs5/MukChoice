package com.project.mukchoice.facade

import com.project.mukchoice.model.location.LocationDto
import com.project.mukchoice.model.location.LocationEntity
import com.project.mukchoice.model.location.LocationResponse
import com.project.mukchoice.service.LocationService
import com.project.mukchoice.util.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationFacade(
    private val locationService: LocationService
) {
    @Transactional
    fun addLocation(address: String): LocationResponse {
        val locationDto: LocationDto = locationService.addLocation(address)

        return LocationResponse().apply {
            location = locationDto
        }
    }

    fun getLocations(): LocationResponse {
        val user = ContextHolder.getUserInfoWithCheck()
        val locations = locationService.getLocationsByUserNo(user.userNo!!)

        val locationDtos = locations.map { location ->
            LocationDto.fromEntity(location)
        }

        return LocationResponse().apply {
            this.locations = locationDtos
        }
    }

    @Transactional
    fun pickLocation(selectedLocationNo: Int) {
        val user = ContextHolder.getUserInfoWithCheck()
        val locations = locationService.getLocationsByUserNo(user.userNo!!)

        locations.forEach { location ->
            location.isSelected = (location.locationNo == selectedLocationNo)
        }
    }

    @Transactional
    fun removeLocation(locationNo: Int) {
        val user = ContextHolder.getUserInfoWithCheck()
        locationService.removeLocation(locationNo, user.userNo!!)
    }
}