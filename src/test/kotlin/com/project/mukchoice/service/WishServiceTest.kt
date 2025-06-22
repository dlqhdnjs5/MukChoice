package com.project.mukchoice.service

import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import com.project.mukchoice.model.place.PlaceEntity
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.model.wish.WishDto
import com.project.mukchoice.model.wish.WishEntity
import com.project.mukchoice.model.wish.WishListResponse
import com.project.mukchoice.repository.PlaceRepository
import com.project.mukchoice.repository.WishRepository
import com.project.mukchoice.util.ContextHolder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class WishServiceTest {
    @Mock
    private lateinit var wishRepository: WishRepository
    @Mock
    private lateinit var placeRepository: PlaceRepository
    @InjectMocks
    private lateinit var wishService: WishService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val userDto = UserDto(
            userNo = 1,
            email = "test@example.com",
            nickName = "테스트유저",
            statusCode = UserStatusCode.ACTIVE,
            typeCode = UserTypeCode.KAKAO,
            imgPath = null,
            lastLoginTime = LocalDateTime.now(),
            regTime = LocalDateTime.now(),
            modTime = LocalDateTime.now()
        )
        ContextHolder.putUser(userDto)
    }

    @Test
    fun `getWishList list`() {
        // given
        val placeEntity = PlaceEntity(
            id = 100L,
            placeName = "테스트장소",
            placeCategory = com.project.mukchoice.consts.PlaceCategory.KOREAN_FOOD,
            phone = "010-1234-5678",
            addressName = "서울시 강남구",
            roadAddressName = "서울시 강남구 테헤란로",
            x = 127.0,
            y = 37.0,
            placeUrl = "http://test.com",
            regTime = LocalDateTime.now(),
            modTime = LocalDateTime.now(),
        )
        val wishEntity = WishEntity(userNo = 1, placeId = 100L, regTime = LocalDateTime.now(), place = placeEntity)
        val wishEntities = listOf(wishEntity)
        whenever(wishRepository.findByUserNoWithPagingWithPlace(1, 0, 20)).thenReturn(wishEntities)
        whenever(wishRepository.countByUserNo(1)).thenReturn(1L)

        // when
        val (wishList, total) = wishService.getWishList(0, 20)
        val result = WishListResponse(wishList, total)

        // then
        assertEquals(1, result.wishList.size)
        val wishDto = result.wishList[0]
        assertEquals(1, wishDto.userNo)
        assertEquals(100L, wishDto.placeId)
        val expectedPlaceDto = PlaceDto.fromEntity(placeEntity).apply { isWish = true }
        assertEquals(expectedPlaceDto, wishDto.place)
    }
}
