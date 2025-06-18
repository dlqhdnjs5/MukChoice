package com.project.mukchoice.controller

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.facade.PlaceFacade
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeFacade: PlaceFacade
) {
    @GetMapping
    fun getPlaces(
        @RequestParam coordinateX: String,
        @RequestParam coordinateY: String,
        @RequestParam(required = false) query: PlaceCategory?,
        @RequestParam(required = false, defaultValue = "1") page: Int
    ): PlaceResponse {
        return placeFacade.getPlaces(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            query = query,
            page = page
        )
    }

    @PostMapping("/wishList")
    fun addWishList(@RequestBody addWishListRequest: AddWishListRequest) {
        placeFacade.addWishList(addWishListRequest)
    }
}