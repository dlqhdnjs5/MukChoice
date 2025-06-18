package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.model.place.AddWishListRequest
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.service.PlaceService
import com.project.mukchoice.service.WishService
import com.project.mukchoice.util.ContextHolder
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PlaceFacade(
    private val placeService: PlaceService,
    private val wishService: WishService
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

    @Transactional
    fun addWishList(addWishListRequest: AddWishListRequest) {
        val userInfo = ContextHolder.getUserInfoWithCheck()
        val document = placeService.searchPlace(addWishListRequest.x, addWishListRequest.y, addWishListRequest.placeName)
            ?: throw IllegalArgumentException("해당 장소가 존재하지 않습니다.")

        if (addWishListRequest.placeId != document.id) {
            throw IllegalArgumentException("존재하지 않는 장소입니다.")
        }

        val placeDto = PlaceDto.fromDocument(document).apply {
            this.placeCategory = addWishListRequest.placeCategory
        }

        placeService.savePlace(placeDto)
        wishService.updateWish(
            userNo = userInfo.userNo!!,
            placeId = placeDto.id.toLong(),
            isWish = addWishListRequest.isWish
        )
    }
}