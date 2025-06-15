package com.project.mukchoice.controller

import com.project.mukchoice.facade.LocationFacade
import com.project.mukchoice.model.location.LocationRequest
import com.project.mukchoice.model.location.LocationResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations")
class LocationController(
    private val locationFacade: LocationFacade
) {
    @PostMapping
    fun addLocation(@RequestBody locationRequest: LocationRequest): LocationResponse {
        requireNotNull(locationRequest.address) { "Address must not be null." }
        return locationFacade.addLocation(locationRequest.address)
    }

    @GetMapping
    fun getLocations(): LocationResponse {
        return locationFacade.getLocations()
    }

    @PutMapping("/pick")
    fun pickLocation(@RequestBody locationRequest: LocationRequest) {
        requireNotNull(locationRequest.selectedLocationNo) { "selectedLocationId must not be null." }
        locationFacade.pickLocation(locationRequest.selectedLocationNo)
    }

    @DeleteMapping("/{locationNo}")
    fun removeLocation(@PathVariable locationNo: String) {
        locationFacade.removeLocation(locationNo.toInt())
    }
}