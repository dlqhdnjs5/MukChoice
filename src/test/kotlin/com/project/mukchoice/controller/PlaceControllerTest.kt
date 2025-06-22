package com.project.mukchoice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.mukchoice.facade.PlaceFacade
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.place.PlaceEntity
import com.project.mukchoice.model.wish.WishDto
import com.project.mukchoice.model.wish.WishListResponse
import com.project.mukchoice.service.UserService
import com.project.mukchoice.service.WishService
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
class PlaceControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var wishService: WishService

    @MockBean
    lateinit var userService: UserService

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
        given(placeFacade.getWishList(0, 20, 0)).willReturn(response)

        // when & then
        mockMvc.get("/places/wishes?offset=0&limit=20&currentLocationNo=0")
            .andExpect {
                status { isOk() }
                content { json(objectMapper.writeValueAsString(response)) }
            }
    }
}
