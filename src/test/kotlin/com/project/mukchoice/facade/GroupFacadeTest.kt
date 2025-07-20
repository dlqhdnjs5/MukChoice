package com.project.mukchoice.facade

import com.project.mukchoice.consts.PlaceCategory
import com.project.mukchoice.consts.UserStatusCode
import com.project.mukchoice.consts.UserTypeCode
import com.project.mukchoice.model.group.*
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.service.GroupService
import com.project.mukchoice.service.PlaceService
import com.project.mukchoice.service.WishService
import com.project.mukchoice.util.ContextHolder
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class GroupFacadeTest {

    private val groupService: GroupService = mockk()
    private val placeService: PlaceService = mockk()
    private val wishService: WishService = mockk()
    private lateinit var groupFacade: GroupFacade

    private val testUserNo = 123
    private val testGroupId = 456L
    private val testPlaceId = 789L
    private val testUserDto = UserDto(
        userNo = testUserNo,
        email = "test@example.com",
        nickName = "TestUser",
        statusCode = UserStatusCode.ACTIVE,
        typeCode = UserTypeCode.KAKAO,
        imgPath = "test.jpg",
        lastLoginTime = LocalDateTime.now(),
        regTime = LocalDateTime.now(),
        modTime = LocalDateTime.now()
    )

    @BeforeEach
    fun setUp() {
        groupFacade = GroupFacade(groupService, placeService, wishService)
        clearAllMocks()

        // ContextHolder mock 설정
        mockkObject(ContextHolder)
        every { ContextHolder.getUserInfoWithCheck() } returns testUserDto
    }

    @Test
    fun `addPlaceToGroup - successfully adds place to group`() {
        // Given
        val addPlaceRequest = AddPlaceToGroupRequest(
            x = "127.123",
            y = "37.456",
            placeName = "테스트 장소",
            placeId = testPlaceId,
            placeCategory = PlaceCategory.KOREAN_FOOD
        )

        every { placeService.validateAndSavePlace(any(), any(), any(), any(), any()) }
        every { groupService.addPlaceToGroup(any(), any(), any()) }

        // When
        groupFacade.addPlaceToGroup(testGroupId, addPlaceRequest)

        // Then
        verify(exactly = 1) {
            placeService.validateAndSavePlace(
                x = addPlaceRequest.x,
                y = addPlaceRequest.y,
                placeName = addPlaceRequest.placeName,
                placeId = addPlaceRequest.placeId,
                placeCategory = addPlaceRequest.placeCategory
            )
        }
        verify(exactly = 1) {
            groupService.addPlaceToGroup(testUserNo, testGroupId, addPlaceRequest)
        }
    }

    @Test
    fun `addPlaceToGroup - throws exception when retrieving user info fails`() {
        // Given
        every { ContextHolder.getUserInfoWithCheck() } throws IllegalArgumentException("사용자 정보를 찾을 수 없습니다")

        val addPlaceRequest = AddPlaceToGroupRequest(
            x = "127.123",
            y = "37.456",
            placeName = "테스트 장소",
            placeId = testPlaceId,
            placeCategory = PlaceCategory.KOREAN_FOOD
        )

        // When & Then
        assertThrows<IllegalArgumentException> {
            groupFacade.addPlaceToGroup(testGroupId, addPlaceRequest)
        }

        verify(exactly = 0) { placeService.validateAndSavePlace(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { groupService.addPlaceToGroup(any(), any(), any()) }
    }

    @Test
    fun `getGroupDetail - returns group detail with places`() {
        // Given
        val testGroup = createTestGroupEntity()
        val testPlaces = listOf(
            createTestPlaceDto(1L, "장소1"),
            createTestPlaceDto(2L, "장소2")
        )

        every { groupService.getGroupWithDetailById(testGroupId) } returns testGroup
        every { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) } just Runs
        every { groupService.getPlacesWithDetailsByGroupId(testGroupId) } returns testPlaces
        every { wishService.existsWish(testUserNo, 1L) } returns true
        every { wishService.existsWish(testUserNo, 2L) } returns false

        // When
        val result = groupFacade.getGroupDetail(testGroupId)

        // Then
        assertEquals(testGroupId, result.groupId)
        assertEquals("테스트 그룹", result.groupName)
        assertEquals(2, result.memberCount)
        assertEquals(2, result.placeCount)
        assertEquals(2, result.places.size)


        verify(exactly = 1) { groupService.getGroupWithDetailById(testGroupId) }
        verify(exactly = 1) { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) }
        verify(exactly = 1) { groupService.getPlacesWithDetailsByGroupId(testGroupId) }
        verify(exactly = 1) { wishService.existsWish(testUserNo, 1L) }
        verify(exactly = 1) { wishService.existsWish(testUserNo, 2L) }
    }

    @Test
    fun `getGroupDetail - returns group detail without places`() {
        // Given
        val testGroup = createTestGroupEntity()
        val emptyPlaces = emptyList<PlaceDto>()

        every { groupService.getGroupWithDetailById(testGroupId) } returns testGroup
        every { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) } just Runs
        every { groupService.getPlacesWithDetailsByGroupId(testGroupId) } returns emptyPlaces

        // When
        val result = groupFacade.getGroupDetail(testGroupId)

        // Then
        assertEquals(testGroupId, result.groupId)
        assertEquals("테스트 그룹", result.groupName)
        assertEquals(2, result.memberCount)
        assertEquals(0, result.placeCount)
        assertTrue(result.places.isEmpty())

        verify(exactly = 1) { groupService.getGroupWithDetailById(testGroupId) }
        verify(exactly = 1) { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) }
        verify(exactly = 1) { groupService.getPlacesWithDetailsByGroupId(testGroupId) }
        verify(exactly = 0) { wishService.existsWish(any(), any()) }
    }

    @Test
    fun `getGroupDetail - throws exception for non-member`() {
        // Given
        val testGroup = createTestGroupEntity()

        every { groupService.getGroupWithDetailById(testGroupId) } returns testGroup
        every { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) } throws
            IllegalArgumentException("그룹 멤버가 아닙니다")

        // When & Then
        assertThrows<IllegalArgumentException> {
            groupFacade.getGroupDetail(testGroupId)
        }

        verify(exactly = 1) { groupService.getGroupWithDetailById(testGroupId) }
        verify(exactly = 1) { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) }
        verify(exactly = 0) { groupService.getPlacesWithDetailsByGroupId(any()) }
    }

    @Test
    fun `getGroupDetail - throws exception when group does not exist`() {
        // Given
        every { groupService.getGroupWithDetailById(testGroupId) } throws
            IllegalArgumentException("그룹을 찾을 수 없습니다")

        // When & Then
        assertThrows<IllegalArgumentException> {
            groupFacade.getGroupDetail(testGroupId)
        }

        verify(exactly = 1) { groupService.getGroupWithDetailById(testGroupId) }
        verify(exactly = 0) { groupService.checkGroupMember(any()) }
        verify(exactly = 0) { groupService.getPlacesWithDetailsByGroupId(any()) }
    }

    @Test
    fun `getGroupDetail - throws exception when retrieving user info fails`() {
        // Given
        every { ContextHolder.getUserInfoWithCheck() } throws IllegalArgumentException("사용자 정보를 찾을 수 없습니다")

        // When & Then
        assertThrows<IllegalArgumentException> {
            groupFacade.getGroupDetail(testGroupId)
        }

        verify(exactly = 0) { groupService.getGroupWithDetailById(any()) }
        verify(exactly = 0) { groupService.checkGroupMember(any()) }
    }

    @Test
    fun `getGroupDetail - verifies group member info mapping`() {
        // Given
        val testGroup = createTestGroupEntityWithMembers()
        val emptyPlaces = emptyList<PlaceDto>()

        every { groupService.getGroupWithDetailById(testGroupId) } returns testGroup
        every { groupService.checkGroupMember(UserGroupId(testUserNo, testGroupId)) } just Runs
        every { groupService.getPlacesWithDetailsByGroupId(testGroupId) } returns emptyPlaces

        // When
        val result = groupFacade.getGroupDetail(testGroupId)

        // Then
        assertEquals(2, result.members.size)

        // 첫 번째 멤버 (소유자)
        val owner = result.members.find { it.isOwner }
        assertEquals(testUserNo, owner?.userNo)
        assertEquals("test@example.com", owner?.email)
        assertEquals("TestUser", owner?.nickName)
        assertTrue(owner?.isOwner == true)

        // 두 번째 멤버 (일반 멤버)
        val member = result.members.find { !it.isOwner }
        assertEquals(456, member?.userNo)
        assertEquals("member@example.com", member?.email)
        assertEquals("Member", member?.nickName)
        assertFalse(member?.isOwner == true)
    }

    private fun createTestGroupEntity(): GroupEntity {
        return GroupEntity(
            groupId = testGroupId,
            groupName = "테스트 그룹",
            regTime = LocalDateTime.now(),
            modTime = LocalDateTime.now(),
            members = listOf(
                createTestUserGroupEntity(testUserNo, true),
                createTestUserGroupEntity(456, false)
            )
        )
    }

    private fun createTestGroupEntityWithMembers(): GroupEntity {
        return GroupEntity(
            groupId = testGroupId,
            groupName = "테스트 그룹",
            regTime = LocalDateTime.now(),
            modTime = LocalDateTime.now(),
            members = listOf(
                createTestUserGroupEntityWithUser(testUserNo, true, "test@example.com", "TestUser"),
                createTestUserGroupEntityWithUser(456, false, "member@example.com", "Member")
            )
        )
    }

    private fun createTestUserGroupEntity(userNo: Int, isOwner: Boolean): UserGroupEntity {
        return mockk<UserGroupEntity>().apply {
            every { this@apply.userNo } returns userNo
            every { this@apply.isOwner } returns isOwner
            every { this@apply.user } returns null
        }
    }

    private fun createTestUserGroupEntityWithUser(
        userNo: Int,
        isOwner: Boolean,
        email: String,
        nickName: String
    ): UserGroupEntity {
        val mockUser = mockk<com.project.mukchoice.model.user.UserEntity>().apply {
            every { this@apply.email } returns email
            every { this@apply.nickName } returns nickName
            every { this@apply.imgPath } returns "test.jpg"
        }

        return mockk<UserGroupEntity>().apply {
            every { this@apply.userNo } returns userNo
            every { this@apply.isOwner } returns isOwner
            every { this@apply.user } returns mockUser
        }
    }

    private fun createTestPlaceDto(id: Long, name: String): PlaceDto {
        return PlaceDto(
            id = id.toString(),
            placeName = name,
            categoryName = "음식점",
            categoryGroupCode = "FD6",
            categoryGroupName = "음식점",
            phone = "02-1234-5678",
            addressName = "서울시 강남구",
            roadAddressName = "테헤란로 123",
            x = "127.123",
            y = "37.456",
            bcode = "1168010600",
            dong = "역삼동",
            placeUrl = "http://example.com",
            thumbnailUrl = null,
            distance = "100",
            totalCount = null,
            isEnd = null,
            placeCategory = null,
            isWish = false
        )
    }
}
