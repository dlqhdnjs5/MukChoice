package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.model.place.PlaceResponse
import com.project.mukchoice.service.PlaceService
import org.springframework.stereotype.Service

@Service
class PlaceFacade(
    private val placeService: PlaceService,
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
        val places = placeService.getPlaces(coordinateX, coordinateY, query, page)
        return PlaceResponse(places)
    }
}