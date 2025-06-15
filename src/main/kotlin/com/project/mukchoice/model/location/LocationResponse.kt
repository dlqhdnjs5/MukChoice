package com.project.mukchoice.model.location

data class LocationResponse(
    var location: LocationDto? = null,
    var locations: List<LocationDto>? = emptyList()
)
