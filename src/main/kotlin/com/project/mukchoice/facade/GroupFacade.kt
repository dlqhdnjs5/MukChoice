package com.project.mukchoice.facade

import com.project.mukchoice.model.group.*
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.service.GroupService
import com.project.mukchoice.service.PlaceService
import com.project.mukchoice.service.WishService
import com.project.mukchoice.util.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupFacade(
    private val groupService: GroupService,
    private val placeService: PlaceService,
    private val wishService: WishService,
) {
    @Transactional
    fun addPlaceToGroup(groupId: Long, addPlaceToGroupRequest: AddPlaceToGroupRequest) {
        val userDto = ContextHolder.getUserInfoWithCheck()

        placeService.validateAndSavePlace(
            x = addPlaceToGroupRequest.x,
            y = addPlaceToGroupRequest.y,
            placeName = addPlaceToGroupRequest.placeName,
            placeId = addPlaceToGroupRequest.placeId,
            placeCategory = addPlaceToGroupRequest.placeCategory
        )

        groupService.addPlaceToGroup(userDto.userNo!!, groupId, addPlaceToGroupRequest)
    }

    fun getGroupDetail(groupId: Long): GroupDetailResponse {
        val userDto = ContextHolder.getUserInfoWithCheck()
        val userNo = userDto.userNo!!

        val group: GroupEntity = groupService.getGroupWithDetailById(groupId)
        groupService.checkGroupMember(UserGroupId(userNo, groupId))

        val members = group.members.map { userGroup ->
            GroupMember(
                userNo = userGroup.userNo,
                isOwner = userGroup.isOwner,
                email = userGroup.user?.email ?: "",
                nickName = userGroup.user?.nickName ?: "",
                imgPath = userGroup.user?.imgPath
            )
        }

        val placeDtos: List<PlaceDto> = groupService.getPlacesWithDetailsByGroupId(groupId)
        if (placeDtos.isEmpty()) {
            return GroupDetailResponse(
                groupId = group.groupId!!,

                groupName = group.groupName,
                regTime = group.regTime!!,
                modTime = group.modTime!!,
                members = members,
                memberCount = members.size,
                places = emptyList(),
                placeCount = 0
            )
        }
        placeDtos.apply {
            forEach { place ->
                place.isWish = wishService.existsWish(userNo, place.id.toLong())
            }
        }

        return GroupDetailResponse(
            groupId = group.groupId!!,
            groupName = group.groupName,
            regTime = group.regTime!!,
            modTime = group.modTime!!,
            members = members,
            memberCount = members.size,
            places = placeDtos,
            placeCount = placeDtos.size
        )
    }
}