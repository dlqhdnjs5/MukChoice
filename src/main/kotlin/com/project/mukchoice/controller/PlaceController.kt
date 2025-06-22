package com.project.mukchoice.controller

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.facade.PlaceFacade
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.model.wish.WishListResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeFacade: PlaceFacade,
    private val wishFacade: PlaceFacade
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

    @GetMapping("/wishes")
    fun getWishList(@RequestParam offset: Int = 0,@RequestParam limit: Int = 20, @RequestParam(required = false, defaultValue = "1") currentLocationNo: Int?
    ): WishListResponse {
        return wishFacade.getWishList(offset, limit, currentLocationNo)
    }
}