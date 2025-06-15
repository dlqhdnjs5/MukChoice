package com.project.mukchoice.service

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.manager.ChromeDriverManager
import com.project.mukchoice.manager.HttpWebClientManager
import com.project.mukchoice.model.place.Document
import com.project.mukchoice.model.place.KakaoImageSearchResponse
import com.project.mukchoice.model.place.KakaoKeywordSearchPlaceResponse
import com.project.mukchoice.model.place.PlaceDto
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class PlaceService(
    @Value("\${kakao.rest.api.key}") val kakaoRestApiKey: String,
    private val httpWebClientManager: HttpWebClientManager,
    private val chromeDriverManager: ChromeDriverManager
) {
    companion object {
        private const val RADIUS = 1000
        private const val SIZE = 10
    }
    fun searchPlaces(coordinateX: String, coordinateY: String, query: PlaceCategory?, page: Int): List<Document>? {
        val actualQuery = query?.displayName ?: PlaceCategory.ALL.displayName
        val url = "https://dapi.kakao.com/v2/local/search/keyword?" +
                "query=${actualQuery}&x=${coordinateX}&y=${coordinateY}&radius=${RADIUS}&page=${page}&size=${SIZE}&sort=distance"
        val headers = HttpHeaders().apply {
            add("Authorization", "KakaoAK $kakaoRestApiKey")
        }

        try {
            return httpWebClientManager.get(url, headers, KakaoKeywordSearchPlaceResponse::class.java)?.documents
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching for places: ${e.message}", e)
        }
    }

    /**
     * 카카오 이미지 검색 API를 사용하여 장소의 이미지를 검색. 하지만 정확성이 너무 떨어져서 사용 X
     * @param address 장소의 주소
     * @param placeName 장소의 이름
     * @return 이미지 URL 또는 null
     */
    fun searchPlacesImg(address: String?, placeName: String): String? {
        val query = "$address $placeName"
        val url = "https://dapi.kakao.com/v2/search/image?query=$query&size=5"
        val headers = HttpHeaders().apply {
            add("Authorization", "KakaoAK $kakaoRestApiKey")
        }

        val result = httpWebClientManager.get(url, headers, KakaoImageSearchResponse::class.java)

        return result?.meta?.total_count?.let {
            result.documents.get(0).image_url
        }
    }

    fun getPlaces(coordinateX: String, coordinateY: String, query: PlaceCategory?, page: Int): List<PlaceDto> {
        return searchPlaces(coordinateX, coordinateY, query, page)
            ?.mapNotNull { it?.let { PlaceDto.fromDocument(it) } }
            ?: emptyList()
    }

    /**
     * 병렬로 장소를 검색하고 각 장소에 대한 썸네일을 크롤링하는 함수
     * cpu사용량 문제와, 저작권 문제로  보류
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param query 검색어
     * @param page 페이지 번호
     * @return 장소 DTO 리스트
     */
    suspend fun getPlacesParallel(coordinateX: String, coordinateY: String, query: PlaceCategory, page: Int): List<PlaceDto> {
        val places = searchPlaces(coordinateX, coordinateY, query, page) ?: return emptyList()

        return coroutineScope {
            places.map { place ->
                async {
                    fetchPlaceDtoWithThumbnail(place)
                }
            }.awaitAll()
        }
    }

    private suspend fun fetchPlaceDtoWithThumbnail(place: Document): PlaceDto = withContext(Dispatchers.IO) {
        delay(200)
        val thumbnailUrl = chromeDriverManager.getKakaoPlaceThumbnailUrl(place.place_url)
        return@withContext thumbnailUrl?.let {
            PlaceDto.fromDocument(place).apply {
                this.thumbnailUrl = it
            }
        } ?: PlaceDto.fromDocument(place)
    }
}