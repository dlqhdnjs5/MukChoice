package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.model.location.LocationEntity
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.model.wish.WishDongInfoResponse
import com.project.mukchoice.model.wish.WishDto
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

    // TODO 추후 f/o
    /*fun getPlaces(coordinateX: String, coordinateY: String, query: PlaceCategory?, page: Int): PlaceResponse {
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
    }*/

    /**
     * 장소를 검색하는 API
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param query 검색어
     * @return PlaceResponse 객체에 장소 리스트가 포함됨
     */
    fun getPlacesWithQuery(coordinateX: String, coordinateY: String, query: String): PlaceResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        placeService.searchPlacesWithQuery(coordinateX, coordinateY, query)
            .map { PlaceDto.fromDocument(it) }
            .let { places ->
                if (places.isEmpty()) {
                    return PlaceResponse(emptyList())
                }

                places.forEach { place ->
                    place.isWish = wishService.existsWish(userInfo.userNo!!, place.id.toLong())
                }

                return PlaceResponse(places)
            }
    }

    fun getPlacesMultiCategory(
        coordinateX: String,
        coordinateY: String,
        categories: List<PlaceCategory>?,
        page: Int
    ): PlaceResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()

        if (categories.isNullOrEmpty() || categories[0] == PlaceCategory.ALL) {
            val result = placeService.searchAllPlaces(coordinateX, coordinateY)
                .map { PlaceDto.fromDocument(it) }

            return PlaceResponse(result)
        }

        val uniquePlaces = categories.asSequence()
            .flatMap { category ->
                placeService.getPlaces(coordinateX, coordinateY, category).asSequence()
            }
            .distinctBy { it.id }
            .onEach { place ->
                place.isWish = wishService.existsWish(userInfo.userNo!!, place.id.toLong())
            }
            .toList()

        return PlaceResponse(uniquePlaces)
    }

    @Transactional
    fun addWishList(addWishListRequest: AddWishListRequest) {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val userNo = userInfo.userNo!!
        val placeId = addWishListRequest.placeId.toLong()

        placeService.validateAndSavePlace(
            x = addWishListRequest.x,
            y = addWishListRequest.y,
            placeName = addWishListRequest.placeName,
            placeId = placeId,
        )

        wishService.updateWish(
            userNo = userNo,
            placeId = placeId,
            isWish = addWishListRequest.isWish
        )
    }

    fun getWishList(offset: Int, limit: Int, currentLocationNo: Int?, bcode: String): WishListResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val userNo = userInfo.userNo ?: throw IllegalArgumentException("User not found")

        // bcode가 "All"이 아니고 값이 존재한다면 DB에서 바로 필터링하여 조회
        val wishListWithTotal = if (!bcode.isNullOrEmpty() && bcode.lowercase() != "all") {
            wishService.getWishListByBcodeWithTotalCount(
                userNo = userNo,
                bcode = bcode,
                offset = offset,
                limit = limit
            )
        } else {
            wishService.getWishListWithTotalCount(
                userNo = userNo,
                offset = offset,
                limit = limit
            )
        }

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

    fun getWishDongList(): WishDongInfoResponse {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val wishDongInfos = wishService.getWishDongList(userInfo.userNo!!)
        return WishDongInfoResponse(wishDongInfos)
    }
}