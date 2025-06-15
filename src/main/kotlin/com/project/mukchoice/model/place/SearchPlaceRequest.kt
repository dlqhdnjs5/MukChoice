package com.project.mukchoice.model.place

data class SearchPlaceRequest(
    val coordinateX: String,
    val coordinateY: String,
    val query: String,
    val page: Int
)
