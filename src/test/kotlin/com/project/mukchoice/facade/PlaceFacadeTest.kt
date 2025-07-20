package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.model.place.Document
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.service.LocationService
import com.project.mukchoice.service.PlaceService
import com.project.mukchoice.service.WishService
import com.project.mukchoice.util.ContextHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class PlaceFacadeTest {

    @Mock
    lateinit var placeService: PlaceService

    @Mock
    lateinit var wishService: WishService

    @Mock
    lateinit var locationService: LocationService

    @InjectMocks
    lateinit var placeFacade: PlaceFacade

    private val coordinateX = "126.61776532832"
    private val coordinateY = "37.5523173127979"
    private val page = 1

    @BeforeEach
    fun setUp() {
        val mockStatic = Mockito.mockStatic(ContextHolder::class.java)

        val userDto = UserDto(
            userNo = 1,
            email = "test@test.com",
            nickName = "testuser",
            statusCode = com.project.mukchoice.consts.UserStatusCode.ACTIVE,
            typeCode = com.project.mukchoice.consts.UserTypeCode.KAKAO,
            imgPath = null,
            lastLoginTime = null,
            regTime = java.time.LocalDateTime.now(),
            modTime = java.time.LocalDateTime.now()
        )

        mockStatic.`when`( ContextHolder.getUserInfoWithCheck() )
            .thenReturn(userDto)
    }

    @Test
    fun `getPlacesMultiCategory multiple categories`() {
        // given
        val koreanFoodDocument = Document(
            id = "1",
            place_name = "테스트 한식당",
            category_name = "음식점 > 한식",
            category_group_code = "FD6",
            category_group_name = "음식점",
            phone = "02-123-4567",
            address_name = "서울시 강남구",
            road_address_name = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            place_url = "https://place.kakao.com/1",
            distance = "100"
        )

        val thaiFoodDocument = Document(
            id = "2",
            place_name = "테스트 태국음식",
            category_name = "음식점 > 태국음식",
            category_group_code = "FD6",
            category_group_name = "음식점",
            phone = "02-123-4568",
            address_name = "서울시 강남구",
            road_address_name = "서울시 강남구 역삼로",
            x = "127.1",
            y = "37.6",
            place_url = "https://place.kakao.com/2",
            distance = "200"
        )

        val koreanPlaceDto = PlaceDto.fromDocumentByCategory(koreanFoodDocument, PlaceCategory.KOREAN_FOOD)
        val thaiPlaceDto = PlaceDto.fromDocumentByCategory(thaiFoodDocument, PlaceCategory.THAI_FOOD)

        // Mock service methods
        `when`(placeService.getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page))
            .thenReturn(listOf(koreanPlaceDto))
        `when`(placeService.getPlaces(coordinateX, coordinateY, PlaceCategory.THAI_FOOD, page))
            .thenReturn(listOf(thaiPlaceDto))
        `when`(wishService.existsWish(1, 1L)).thenReturn(false)
        `when`(wishService.existsWish(1, 2L)).thenReturn(true)

        // when
        val result = placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            queries = listOf(PlaceCategory.KOREAN_FOOD, PlaceCategory.THAI_FOOD),
            page = page
        )

        // then
        assertEquals(2, result.places.size)
        assertEquals("1", result.places[0].id)
        assertEquals("테스트 한식당", result.places[0].placeName)
        assertEquals(PlaceCategory.KOREAN_FOOD, result.places[0].placeCategory)
        assertEquals(false, result.places[0].isWish)

        assertEquals("2", result.places[1].id)
        assertEquals("테스트 태국음식", result.places[1].placeName)
        assertEquals(PlaceCategory.THAI_FOOD, result.places[1].placeCategory)
        assertEquals(true, result.places[1].isWish)

        verify(placeService).getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page)
        verify(placeService).getPlaces(coordinateX, coordinateY, PlaceCategory.THAI_FOOD, page)
        verify(wishService).existsWish(1, 1L)
        verify(wishService).existsWish(1, 2L)
    }

    @Test
    fun `getPlacesMultiCategory with null queries should return all places`() {
        // given
        val document = Document(
            id = "1",
            place_name = "테스트 음식점",
            category_name = "음식점",
            category_group_code = "FD6",
            category_group_name = "음식점",
            phone = "02-123-4567",
            address_name = "서울시 강남구",
            road_address_name = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            place_url = "https://place.kakao.com/1",
            distance = "100"
        )

        // Mock service methods
        `when`(placeService.searchAllPlaces(coordinateX, coordinateY))
            .thenReturn(listOf(document))

        // when
        val result = placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            queries = null,
            page = page
        )

        // then
        assertEquals(1, result.places.size)
        assertEquals("1", result.places[0].id)
        assertEquals("테스트 음식점", result.places[0].placeName)
        assertEquals(PlaceCategory.ALL, result.places[0].placeCategory)

        verify(placeService).searchAllPlaces(coordinateX, coordinateY)
        verifyNoMoreInteractions(wishService) // isWish가 설정되지 않음을 확인
    }

    @Test
    fun `getPlacesMultiCategory with ALL category should return all places`() {
        // given
        val document = Document(
            id = "1",
            place_name = "테스트 음식점",
            category_name = "음식점",
            category_group_code = "FD6",
            category_group_name = "음식점",
            phone = "02-123-4567",
            address_name = "서울시 강남구",
            road_address_name = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            place_url = "https://place.kakao.com/1",
            distance = "100"
        )

        // Mock service methods
        `when`(placeService.searchAllPlaces(coordinateX, coordinateY))
            .thenReturn(listOf(document))

        // when
        val result = placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            queries = listOf(PlaceCategory.ALL),
            page = page
        )

        // then
        assertEquals(1, result.places.size)
        assertEquals("1", result.places[0].id)
        assertEquals("테스트 음식점", result.places[0].placeName)
        assertEquals(PlaceCategory.ALL, result.places[0].placeCategory)

        verify(placeService).searchAllPlaces(coordinateX, coordinateY)
        verifyNoMoreInteractions(wishService) // isWish가 설정되지 않음을 확인
    }

    @Test
    fun `getPlacesMultiCategory should handle empty results`() {
        // given
        `when`(placeService.getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page))
            .thenReturn(emptyList())

        // when
        val result = placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            queries = listOf(PlaceCategory.KOREAN_FOOD),
            page = page
        )

        // then
        assertTrue(result.places.isEmpty())
        verify(placeService).getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page)
        verifyNoMoreInteractions(wishService)
    }

    @Test
    fun `getPlacesMultiCategory remove duplicates when multiple categories return same place`() {
        // given
        val document = Document(
            id = "1",
            place_name = "테스트 음식점",
            category_name = "음식점 > 한식, 일식",
            category_group_code = "FD6",
            category_group_name = "음식점",
            phone = "02-123-4567",
            address_name = "서울시 강남구",
            road_address_name = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            place_url = "https://place.kakao.com/1",
            distance = "100"
        )

        val koreanPlaceDto = PlaceDto.fromDocumentByCategory(document, PlaceCategory.KOREAN_FOOD)
        val japanesePlaceDto = PlaceDto.fromDocumentByCategory(document, PlaceCategory.JAPANESE_FOOD)

        // Mock service methods
        `when`(placeService.getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page))
            .thenReturn(listOf(koreanPlaceDto))
        `when`(placeService.getPlaces(coordinateX, coordinateY, PlaceCategory.JAPANESE_FOOD, page))
            .thenReturn(listOf(japanesePlaceDto))
        `when`(wishService.existsWish(1, 1L)).thenReturn(false)

        // when
        val result = placeFacade.getPlacesMultiCategory(
            coordinateX = coordinateX,
            coordinateY = coordinateY,
            queries = listOf(PlaceCategory.KOREAN_FOOD, PlaceCategory.JAPANESE_FOOD),
            page = page
        )

        // then
        assertEquals(1, result.places.size) // 중복 제거로 하나만 남아야 함
        assertEquals("1", result.places[0].id)
        assertEquals("테스트 음식점", result.places[0].placeName)
        assertEquals(false, result.places[0].isWish)

        verify(placeService).getPlaces(coordinateX, coordinateY, PlaceCategory.KOREAN_FOOD, page)
        verify(placeService).getPlaces(coordinateX, coordinateY, PlaceCategory.JAPANESE_FOOD, page)
        verify(wishService, times(1)).existsWish(1, 1L) // 중복 제거 후 한 번만 호출되어야 함
    }
}

