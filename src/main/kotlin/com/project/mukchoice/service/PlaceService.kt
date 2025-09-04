package com.project.mukchoice.service

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.manager.HttpWebClientManager
import com.project.mukchoice.model.place.*
import com.project.mukchoice.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class PlaceService(
    @Value("\${kakao.rest.api.key}") val kakaoRestApiKey: String,
    private val httpWebClientManager: HttpWebClientManager,
    // private val chromeDriverManager: ChromeDriverManager,
    private val placeRepository: PlaceRepository,
) {
    companion object {
        private const val ONE_KM_RADIUS = 1000
        private const val TWO_KM_RADIUS = 2000
        private const val SIZE = 15
    }

    /**
     * 카카오 API 호출을 위한 공통 헤더 생성
     */
    private fun createKakaoHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            add("Authorization", "KakaoAK $kakaoRestApiKey")
        }
    }

    /**
     * searchPlace를 사용하여 is_end가 true가 될 때까지 여러 페이지를 반복 조회하는 함수, 최대 10번 허용
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param category 가게명
     * @return 모든 Document 리스트
     */
    fun searchCategoryPlaces(coordinateX: String, coordinateY: String, category: PlaceCategory?): List<Document> {
        val actualQuery = category?.displayName ?: throw IllegalArgumentException("Query must not be null")
        return searchPlacesWithPagination { page ->
            "https://dapi.kakao.com/v2/local/search/keyword?" +
                    "query=${actualQuery}&x=${coordinateX}&y=${coordinateY}&radius=${ONE_KM_RADIUS}&page=${page}&size=${SIZE}"
        }
    }

    fun searchAllPlaces(coordinateX: String, coordinateY: String): List<Document> {
        return searchPlacesWithPagination { page ->
            "https://dapi.kakao.com/v2/local/search/category?" +
                    "category_group_code=FD6&x=${coordinateX}&y=${coordinateY}&radius=${ONE_KM_RADIUS}&page=${page}&size=${SIZE}"
        }
    }

    /**
     * 좌표, 가게명으로 가게 존재여부 체크 및 조회
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param query 가게명
     * @return 검색된 장소 리스트
     */
    fun searchExactPlace(coordinateX: String, coordinateY: String, query: String): Document? {
        val radius = 5
        val url = "https://dapi.kakao.com/v2/local/search/keyword?" +
                "query=${query}&x=${coordinateX}&y=${coordinateY}&radius=${radius}&page=1&size=1"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoKeywordSearchPlaceResponse::class.java)?.documents?.get(0)
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching for places: ${e.message}", e)
        }
    }

    fun searchPlacesWithQuery(coordinateX: String, coordinateY: String, query: String?): List<Document> {
        val url = "https://dapi.kakao.com/v2/local/search/keyword?" +
                "query=${query}&x=${coordinateX}&y=${coordinateY}&radius=${TWO_KM_RADIUS}&page=1&size=15&category_group_code=FD6"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoKeywordSearchPlaceResponse::class.java)?.documents ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching for places: ${e.message}", e)
        }
    }

    fun getPlaces(coordinateX: String, coordinateY: String, category: PlaceCategory): List<PlaceDto> {
        return searchCategoryPlaces(coordinateX, coordinateY, category)
            .map { PlaceDto.fromDocument(it) }
    }

    /**
     * 카카오 키워드 검색 API를 사용하여 장소를 여러 페이지에 걸쳐 검색. 최대 10번 호출
     */
    private inline fun searchPlacesWithPagination(createUrl: (Int) -> String): List<Document> {
        val allDocuments = mutableListOf<Document>()
        var page = 1
        var isEnd = false
        var callCount = 0
        val maxCalls = 10

        do {
            if (callCount++ >= maxCalls) break

            val url = createUrl(page)
            val headers = createKakaoHeaders()

            val response = try {
                httpWebClientManager.get(url, headers, KakaoKeywordSearchPlaceResponse::class.java)
            } catch (e: Exception) {
                throw IllegalStateException("Error occurred while searching for places: " + e.message, e)
            }

            response?.documents?.let { allDocuments.addAll(it) }
            isEnd = response?.meta?.is_end ?: true
            page++
        } while (!isEnd)

        return allDocuments
    }

    fun savePlace(placeDto: PlaceDto): PlaceEntity {
        val placeId = placeDto.id.toLong()
        val existing = placeRepository.findById(placeId)
        if (existing != null) return existing
        val entity = PlaceEntity(
            id = placeId,
            placeName = placeDto.placeName,
            placeCategory = placeDto.placeCategory!!,
            phone = placeDto.phone,
            addressName = placeDto.addressName,
            roadAddressName = placeDto.roadAddressName,
            x = placeDto.x.toDouble(),
            y = placeDto.y.toDouble(),
            bcode = placeDto.bcode!!,
            dong = placeDto.dong!!,
            placeUrl = placeDto.placeUrl
        )
        return placeRepository.save(entity)
    }

    /**
     * 카카오 장소 URL이 존재하는지 확인
     * @param placeId 장소 ID
     * @return 존재하면 true, 아니면 false
     */
    fun isValidKakaoPlace(placeId: Long): Boolean {
        val url = "https://place.map.kakao.com/$placeId"

        return try {
            val response = httpWebClientManager.get(url, HttpHeaders(), String::class.java)
            response != null && response.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 주소를 좌표로 변환하는 함수 (단일 결과)
     * @param address 변환할 주소 (예: "서울특별시 강남구 삼성동 159")
     * @return 변환된 좌표 정보 또는 null
     */
    fun searchAddressToCoordinate(address: String): AddressDocument? {
        val url = "https://dapi.kakao.com/v2/local/search/address?query=${address}"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoAddressSearchResponse::class.java)?.documents?.firstOrNull()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching address to coordinate: ${e.message}", e)
        }
    }

    /**
     * 주소를 좌표로 변환하는 함수 (여러 결과)
     * @param address 변환할 주소
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10, 최대 30)
     * @return 변환된 좌표 정보 리스트
     */
    fun searchAddressesToCoordinates(address: String, page: Int = 1, size: Int = 10): List<AddressDocument> {
        val url = "https://dapi.kakao.com/v2/local/search/address?query=${address}&page=${page}&size=${size}"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoAddressSearchResponse::class.java)?.documents ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching addresses to coordinates: ${e.message}", e)
        }
    }

    /**
     * 좌표를 주소로 변환하는 함수 (좌표 → 주소)
     * @param x 경도
     * @param y 위도
     * @return 변환된 주소 정보 또는 null
     */
    fun searchCoordinateToAddress(x: String, y: String): AddressDocument? {
        val url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=${x}&y=${y}&input_coord=WGS84"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoAddressSearchResponse::class.java)?.documents?.firstOrNull()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching coordinate to address: ${e.message}", e)
        }
    }

    /**
     * 좌표를 행정구역정보로 변환하는 함수
     * @param x 경도
     * @param y 위도
     * @return 행정구역 정보 리스트 (행정동과 법정동 정보 포함)
     */
    fun searchCoordinateToDistrict(x: String, y: String): List<DistrictDocument> {
        val url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=${x}&y=${y}&input_coord=WGS84"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoCoordToDistrictResponse::class.java)?.documents ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching coordinate to district: ${e.message}", e)
        }
    }

    /**
     * 좌표를 행정구역정보로 변환하는 함수 (행정동만)
     * @param x 경도
     * @param y 위도
     * @return 행정동 정보 또는 null
     */
    fun searchCoordinateToAdministrativeDistrict(x: String, y: String): DistrictDocument? {
        return searchCoordinateToDistrict(x, y).find { it.region_type == "H" }
    }

    /**
     * 좌표를 행정구역정보로 변환하는 함수 (법정동만)
     * @param x 경도
     * @param y 위도
     * @return 법정동 정보 또는 null
     */
    fun searchCoordinateToLegalDistrict(x: String, y: String): DistrictDocument? {
        return searchCoordinateToDistrict(x, y).find { it.region_type == "B" }
    }

    /**
     * 장소 검증 및 저장을 위한 공통 로직
     * @param x 경도
     * @param y 위도
     * @param placeName 장소명
     * @param placeId 장소 ID
     * @param placeCategory 장소 카테고리
     * @return 저장된 PlaceEntity
     */
    fun validateAndSavePlace(x: String, y: String, placeName: String, placeId: Long): PlaceEntity {
        if (!isValidKakaoPlace(placeId)) {
            throw IllegalArgumentException("존재하지 않는 placeId 입니다.")
        }

        val document = searchExactPlace(x, y, placeName)
            ?: throw IllegalArgumentException("해당 장소가 존재하지 않습니다.")

        if (placeId.toString() != document.id) {
            throw IllegalArgumentException("placeId와 검색된 장소의 ID가 일치하지 않습니다.")
        }

        val districtDocument = searchCoordinateToLegalDistrict(x, y)
            ?: throw IllegalArgumentException("해당 좌표에 대한 법정동 정보가 없습니다.")

        val placeDto = PlaceDto.fromDocument(document).apply {
            this.bcode = districtDocument.code
            this.dong = districtDocument.region_3depth_name
        }

        return savePlace(placeDto)
    }

    /**
     * 장소 DTO를 Document에서 변환하고 썸네일 URL을 크롤링하여 추가하는 함수
     * 현재는 크롤링 기능을 사용하지 않음 (저작권 문제로 보류)
     * @param place 카카오 장소 Document
     * @return PlaceDto 객체
     */
    /*private suspend fun fetchPlaceDtoWithThumbnail(place: Document): PlaceDto = withContext(Dispatchers.IO) {
        delay(200)
        val thumbnailUrl = chromeDriverManager.getKakaoPlaceThumbnailUrl(place.place_url)
        return@withContext thumbnailUrl?.let {
            PlaceDto.fromDocument(place).apply {
                this.thumbnailUrl = it
            }
        } ?: PlaceDto.fromDocument(place)
    }*/


    /**
     * 병렬로 장소를 검색하고 각 장소에 대한 썸네일을 크롤링하는 함수
     * cpu사용량 문제와, 저작권 문제로  보류
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param query 검색어
     * @param page 페이지 번호
     * @return 장소 DTO 리스트
     */
    /*suspend fun getPlacesParallel(coordinateX: String, coordinateY: String, category: PlaceCategory, page: Int): List<PlaceDto> {
        val places = searchCategoryPlaces(coordinateX, coordinateY, category, page) ?: return emptyList()

        return coroutineScope {
            places.map { place ->
                async {
                    fetchPlaceDtoWithThumbnail(place)
                }
            }.awaitAll()
        }
    }*/

    /**
     * 카카오 이미지 검색 API를 사용하여 장소의 이미지를 검색. 하지만 정확성이 너무 떨어져서 사용 X
     * @param address 장소의 주소
     * @param placeName 장소의 이름
     * @return 이미지 URL 또는 null
     */
    /*fun searchPlacesImg(address: String?, placeName: String): String? {
        val query = "$address $placeName"
        val url = "https://dapi.kakao.com/v2/search/image?query=$query&size=5"
        val headers = createKakaoHeaders()

        val result = httpWebClientManager.get(url, headers, KakaoImageSearchResponse::class.java)

        return result?.meta?.total_count?.let {
            result.documents.get(0).image_url
        }
    }*/

    /**
     * 카카오 키워드 검색 API를 사용하여 장소를 검색
     * @param coordinateX X 좌표
     * @param coordinateY Y 좌표
     * @param category 검색어 (PlaceCategory)
     * @param page 페이지 번호
     * @return 검색된 장소 리스트
     */
    /*fun searchCategoryPlaces(coordinateX: String, coordinateY: String, category: PlaceCategory?, page: Int): List<Document>? {
        val actualCategory = category?.displayName ?: PlaceCategory.ALL.displayName
        val url = "https://dapi.kakao.com/v2/local/search/keyword?" +
                "query=${actualCategory}&x=${coordinateX}&y=${coordinateY}&radius=${ONE_KM_RADIUS}&page=${page}&size=${SIZE}&sort=distance"
        val headers = createKakaoHeaders()

        try {
            return httpWebClientManager.get(url, headers, KakaoKeywordSearchPlaceResponse::class.java)?.documents
        } catch (e: Exception) {
            throw IllegalStateException("Error occurred while searching for places: ${e.message}", e)
        }
    }*/
}