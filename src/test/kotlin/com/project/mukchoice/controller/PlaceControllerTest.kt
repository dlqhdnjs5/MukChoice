package com.project.mukchoice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.facade.PlaceFacade
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.wish.WishDto
import com.project.mukchoice.model.wish.WishListResponse
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [PlaceController::class],
    excludeFilters = [
        org.springframework.context.annotation.ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
            classes = [com.project.mukchoice.config.WebMvcConfig::class]
        )
    ]
)
@Suppress("DEPRECATION")
class PlaceControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var placeFacade: PlaceFacade

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `getWishList with response`() {
        // given
        val place = mock(PlaceDto::class.java)
        val wishDto = WishDto(userNo = 1, placeId = 100L, regTime = LocalDateTime.now(), place = place)
        val response = WishListResponse(listOf(wishDto), 1)
        val bcode = "1234"
        given(placeFacade.getWishList(0, 20, 0, bcode)).willReturn(response)

        // when & then
        mockMvc.get("/places/wishes?offset=0&limit=20&currentLocationNo=0")
            .andExpect {
                status { isOk() }
                content { json(objectMapper.writeValueAsString(response)) }
            }
    }

    @Test
    fun `getPlaces multiple categories`() {
        // given
        val placeDto1 = PlaceDto(
            id = "1",
            placeName = "테스트 한식당",
            categoryName = "음식점 > 한식",
            categoryGroupCode = "FD6",
            categoryGroupName = "음식점",
            phone = "02-123-4567",
            addressName = "서울시 강남구",
            roadAddressName = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            bcode = "1168010600",
            dong = "역삼동",
            placeUrl = "https://place.kakao.com/1",
            thumbnailUrl = null,
            distance = "100",
            totalCount = null,
            isEnd = null,
            placeCategory = com.project.mukchoice.consts.PlaceCategory.KOREAN_FOOD,
            isWish = null
        )

        val placeDto2 = PlaceDto(
            id = "2",
            placeName = "테스트 카페",
            categoryName = "음식점 > 태국음식",
            categoryGroupCode = "FD6",
            categoryGroupName = "음식점",
            phone = "02-123-4568",
            addressName = "서울시 강남구",
            roadAddressName = "서울시 강남구 역삼로",
            x = "127.1",
            y = "37.6",
            bcode = "1168010700",
            dong = "삼성동",
            placeUrl = "https://place.kakao.com/2",
            thumbnailUrl = null,
            distance = "200",
            totalCount = null,
            isEnd = null,
            placeCategory = PlaceCategory.ASIAN_FOOD,
            isWish = null
        )

        val response = com.project.mukchoice.model.place.PlaceResponse(listOf(placeDto1, placeDto2))

        given(placeFacade.getPlacesMultiCategory(
            coordinateX = "126.61776532832",
            coordinateY = "37.5523173127979",
            categories = listOf(PlaceCategory.KOREAN_FOOD, PlaceCategory.ASIAN_FOOD),
            page = 1
        )).willReturn(response)

        // when & then
        mockMvc.get("/places/v2") {
            param("coordinateX", "126.61776532832")
            param("coordinateY", "37.5523173127979")
            param("queries", "KOREAN_FOOD")
            param("queries", "THAI_FOOD")
            param("page", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.places[0].id") { value("1") }
            jsonPath("$.places[0].placeName") { value("테스트 한식당") }
            jsonPath("$.places[1].id") { value("2") }
            jsonPath("$.places[1].placeName") { value("테스트 카페") }
            jsonPath("$.places.length()") { value(2) }
        }
    }

    @Test
    fun `getPlaces2 with no category should return all places`() {
        // given
        val placeDto = PlaceDto(
            id = "1",
            placeName = "테스트 음식점",
            categoryName = "음식점",
            categoryGroupCode = "FD6",
            categoryGroupName = "음식점",
            phone = "02-123-4567",
            addressName = "서울시 강남구",
            roadAddressName = "서울시 강남구 테헤란로",
            x = "127.0",
            y = "37.5",
            placeUrl = "https://place.kakao.com/1",
            thumbnailUrl = null,
            distance = "100"
        )

        val response = com.project.mukchoice.model.place.PlaceResponse(listOf(placeDto))

        given(placeFacade.getPlacesMultiCategory(
            coordinateX = "126.61776532832",
            coordinateY = "37.5523173127979",
            categories = null,
            page = 1
        )).willReturn(response)

        // when & then
        mockMvc.get("/places/v2") {
            param("coordinateX", "126.61776532832")
            param("coordinateY", "37.5523173127979")
            param("page", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.places[0].id") { value("1") }
            jsonPath("$.places[0].placeName") { value("테스트 음식점") }
            jsonPath("$.places.length()") { value(1) }
        }
    }
}
