package com.project.mukchoice.controller

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.facade.PlaceFacade
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.model.wish.WishDongInfoResponse
import com.project.mukchoice.model.wish.WishListResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/places")
class PlaceController(
    private val placeFacade: PlaceFacade,
    private val wishFacade: PlaceFacade
) {

    // TODO 추후 f/o
    /**
     * 장소를 조회하는 API
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param query 카테고리 이름 (PlaceCategory의 이름)
     * @param page 페이지 번호, 기본값은 1
     * @return PlaceResponse 객체에 장소 리스트가 포함됨
     */
    /*@GetMapping
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
    }*/

    @GetMapping
    fun getPlacesWithQuery(
        @RequestParam coordinateX: String,
        @RequestParam coordinateY: String,
        @RequestParam query: String,
    ): PlaceResponse {
        return placeFacade.getPlacesWithQuery(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            query = query,
        )
    }

    /**
     * 여러 카테고리로 장소를 조회하는 API
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param queries 카테고리 이름 리스트 (PlaceCategory의 이름)
     * @param page 페이지 번호, 기본값은 1
     * @return PlaceResponse 객체에 장소 리스트가 포함됨
     */
    @GetMapping("/v2")
    fun getPlacesMultiCategory(
        @RequestParam coordinateX: String,
        @RequestParam coordinateY: String,
        @RequestParam(required = false) queries: List<String>?,
        @RequestParam(required = false, defaultValue = "1") page: Int
    ): PlaceResponse {
        val categoryQueries = queries?.mapNotNull { queryString ->
            try {
                PlaceCategory.valueOf(queryString)
            } catch (e: IllegalArgumentException) {
                null // 잘못된 카테고리 이름은 무시
            }
        }

        return placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            categories = categoryQueries,
            page = page
        )
    }

    /**
     * 장소를 찜 목록에 추가하는 API
     * @param addWishListRequest AddWishListRequest 객체에 장소 정보가 포함됨
     */
    @PostMapping("/wishList")
    fun addWishList(@RequestBody addWishListRequest: AddWishListRequest) {
        placeFacade.addWishList(addWishListRequest)
    }

    /**
     * 찜 목록을 조회하는 API
     * @param offset 페이지 오프셋, 기본값은 0
     * @param limit 페이지 크기, 기본값은 20
     * @param currentLocationNo 현재 위치 번호, 선택적 파라미터
     * @return WishListResponse 객체에 찜 목록이 포함됨
     */
    @GetMapping("/wishes")
    fun getWishList(@RequestParam offset: Int = 0,@RequestParam limit: Int = 20, @RequestParam(required = false, defaultValue = "1") currentLocationNo: Int?
    ,@RequestParam(required = false) bcode: String): WishListResponse {
        return wishFacade.getWishList(offset, limit, currentLocationNo, bcode)
    }

    /**
     * 사용자가 찜한 장소들의 고유한 법정동 목록을 조회하는 API
     * @return 법정동 목록
     */
    @GetMapping("/wishes/dongs")
    fun getWishDongList(): WishDongInfoResponse {
        return wishFacade.getWishDongList()
    }
}