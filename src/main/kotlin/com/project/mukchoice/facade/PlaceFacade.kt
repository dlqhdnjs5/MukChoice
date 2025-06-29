package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.model.location.LocationEntity
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.model.wish.WishDto
import com.project.mukchoice.model.wish.WishEntity
import com.project.mukchoice.model.wish.WishListResponse
import com.project.mukchoice.service.LocationService
import com.project.mukchoice.service.PlaceService
import com.project.mukchoice.service.WishService
import com.project.mukchoice.util.ContextHolder
import com.project.mukchoice.util.GeoUtil
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PlaceFacade(
    private val placeService: PlaceService,
    private val wishService: WishService,
    private val locationService: LocationService
) {

    // TODO 이미지 검색을 병렬로 처리하기 위해 코루틴 사용 하였으나 cpu 사용량이 높아짐. 크롤링 해결 되면 변경해볼것.
    /*suspend fun getPlaces(coordinateX: String, coordinateY: String, query: String, page: Int) {
        val startTime = System.currentTimeMillis()
        val places = placeService.getPlacesParallelX(coordinateX, coordinateY, query, page)
        val endTime = System.currentTimeMillis()

        println("Execution time: ${(endTime - startTime) / 1000} seconds") // 실행 시간
        println("end")
    }*/

    fun getPlaces(coordinateX: String, coordinateY: String, query: PlaceCategory?, page: Int): PlaceResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val places: List<PlaceDto> = placeService.getPlaces(coordinateX, coordinateY, query, page)
        places.apply {
            if (isEmpty()) {
                return PlaceResponse(emptyList())
            }

            forEach { place ->
                place.isWish = wishService.existsWish(userInfo.userNo!!, place.id.toLong())
            }
        }
        return PlaceResponse(places)
    }

    fun getPlacesMultiCategory(coordinateX: String, coordinateY: String, queries: List<PlaceCategory>?, page: Int): PlaceResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()

        if (queries.isNullOrEmpty() || queries[0] == PlaceCategory.ALL) {
            val result =  placeService.searchAllPlaces(coordinateX, coordinateY)
                .map { PlaceDto.fromDocumentByCategory(it, PlaceCategory.ALL) }

            return PlaceResponse(result)
        }

        val allPlaces = mutableListOf<PlaceDto>()
        for (query in queries) {
            val places = placeService.getPlaces(coordinateX, coordinateY, query, page)
            allPlaces.addAll(places)
        }

        val uniquePlaces = allPlaces.distinctBy { it.id }
        uniquePlaces.forEach { place ->
            place.isWish = wishService.existsWish(userInfo.userNo!!, place.id.toLong())
        }

        return PlaceResponse(uniquePlaces)
    }

    @Transactional
    fun addWishList(addWishListRequest: AddWishListRequest) {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val document = placeService.searchPlace(addWishListRequest.x, addWishListRequest.y, addWishListRequest.placeName)
            ?: throw IllegalArgumentException("해당 장소가 존재하지 않습니다.")

        if (addWishListRequest.placeId != document.id) {
            throw IllegalArgumentException("존재하지 않는 장소입니다.")
        }

        val placeDto = PlaceDto.fromDocumentByCategory(document, addWishListRequest.placeCategory)
        placeService.savePlace(placeDto)
        wishService.updateWish(
            userNo = userInfo.userNo!!,
            placeId = placeDto.id.toLong(),
            isWish = addWishListRequest.isWish
        )
    }

    fun getWishList(offset: Int, limit: Int, currentLocationNo: Int?): WishListResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val userNo = userInfo.userNo ?: throw IllegalArgumentException("User not found")

        val wishListWithTotal: Pair<List<WishEntity>, Long> = wishService.getWishListWithTotalCount(
            userNo = userNo,
            offset = offset,
            limit = limit
        )
        val wishList = wishListWithTotal.first
        val total = wishListWithTotal.second

        if (wishList.isEmpty()) {
            return WishListResponse(emptyList(), total)
        }

        var currentLocation: LocationEntity? = null
        if (currentLocationNo != null) {
            currentLocation = locationService.getLocationByLocationNo(currentLocationNo)
        }

        val dtoList = wishList.map {
            check(it.place != null) { "Place must not be null" }

            WishDto(
                userNo = it.userNo,
                placeId = it.placeId,
                regTime = it.regTime,
                place = PlaceDto.fromEntity(it.place!!).apply {
                    this.isWish = true

                    if (currentLocation != null) {
                        val currentX = currentLocation.x
                        val currentY = currentLocation.y
                        val targetX = this.x.toDouble()
                        val targetY = this.y.toDouble()
                        this.distance = GeoUtil.distanceMeter(currentY, currentX, targetY, targetX).toInt().toString()
                    }
                }
            )
        }

        return WishListResponse(
            wishList = dtoList,
            total = total
        )
    }
}