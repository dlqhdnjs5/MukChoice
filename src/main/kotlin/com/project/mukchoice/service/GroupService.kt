package com.project.mukchoice.service

import com.project.mukchoice.model.group.*
import com.project.mukchoice.model.user.UserDto
import com.project.mukchoice.model.place.PlaceDto
import com.project.mukchoice.repository.GroupRepository
import com.project.mukchoice.repository.UserGroupRepository
import com.project.mukchoice.repository.GroupPlaceRepository
import com.project.mukchoice.util.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupPlaceRepository: GroupPlaceRepository,
) {

    @Transactional
    fun createGroup(request: CreateGroupRequest): GroupResponse {
        val userDto: UserDto = ContextHolder.getUserInfoWithCheck()
        val userNo = userDto.userNo!!

        // 1. 사용자가 소유한 그룹 개수 확인 (최대 10개 제한)
        val ownedGroupCount = groupRepository.countGroupsByOwner(userNo)
        if (ownedGroupCount >= 10) {
            throw IllegalArgumentException("그룹은 최대 10개까지만 생성할 수 있습니다.")
        }

        // 2. 그룹 생성
        val groupEntity = GroupEntity(request.groupName)
        val savedGroup = groupRepository.save(groupEntity)

        // 3. 그룹 생성자를 owner로 등록
        val userGroupEntity = UserGroupEntity(
            userNo = userNo,
            groupId = savedGroup.groupId!!,
            isOwner = true
        )
        userGroupRepository.save(userGroupEntity)

        // 4. 응답 반환
        return GroupResponse(
            groupId = savedGroup.groupId!!,
            groupName = savedGroup.groupName,
            regTime = savedGroup.regTime!!,
            modTime = savedGroup.modTime!!
        )
    }

    fun addPlaceToGroup(userNo: Int, groupId: Long, request: AddPlaceToGroupRequest) {
        val placeId = request.placeId
        if (groupPlaceRepository.existsByGroupIdAndPlaceId(groupId, placeId)) {
            return
        }

        groupRepository.findById(groupId)
            ?: throw IllegalArgumentException("존재하지 않는 그룹입니다.")

        val userGroupId = UserGroupId(userNo, groupId)
        userGroupRepository.findById(userGroupId)
            ?: throw IllegalArgumentException("그룹에 속하지 않은 사용자입니다.")

        val groupPlaceEntity = GroupPlaceEntity(
            groupId = groupId,
            placeId = placeId,
            register = userNo
        )
        groupPlaceRepository.save(groupPlaceEntity)
    }

    fun getGroupInfoList(): List<GroupListResponse> {
        val userDto = ContextHolder.getUserInfoWithCheck()
        val userNo = userDto.userNo!!

        // 1. 사용자가 속한 그룹 목록 조회 (멤버들도 함께 로드됨)
        val groups = groupRepository.findGroupsByUserNo(userNo)
        if (groups.isEmpty()) {
            return emptyList() // 사용자가 속한 그룹이 없는 경우 빈 리스트 반환
        }

        // 2. 그룹 ID 목록 추출하여 장소 개수 조회
        val groupIds = groups.mapNotNull { it.groupId }
        val placeCountMap = groupRepository.countPlacesByGroupIds(groupIds)

        // 3. 각 그룹의 멤버 정보와 장소 개수를 포함하여 응답 생성
        return groups.map { group ->
            val members = group.members.map { userGroup ->
                GroupMember(
                    userNo = userGroup.userNo,
                    isOwner = userGroup.isOwner,
                    email = userGroup.user?.email ?: "",
                    nickName = userGroup.user?.nickName ?: "",
                    imgPath = userGroup.user?.imgPath
                )
            }

            GroupListResponse(
                groupId = group.groupId!!,
                groupName = group.groupName,
                regTime = group.regTime!!,
                modTime = group.modTime!!,
                members = members,
                memberCount = members.size,
                placeCount = placeCountMap[group.groupId]?.toInt() ?: 0
            )
        }
    }

    fun getGroupWithDetailById(groupId: Long): GroupEntity {
        return groupRepository.findGroupWithDetailsById(groupId)
            ?: throw IllegalArgumentException("존재하지 않는 그룹입니다.")
    }

    fun checkGroupMember(userGroupId: UserGroupId) {
        userGroupRepository.findById(userGroupId)
            ?: throw IllegalArgumentException("그룹에 속하지 않은 사용자입니다.")
    }

    fun getPlacesWithDetailsByGroupId( groupId: Long): List<PlaceDto> {
        val groupPlaces = groupPlaceRepository.findPlacesWithDetailsByGroupId(groupId)
        return groupPlaces.mapNotNull { groupPlace ->
            val place = groupPlace.place
            if (place != null) {
                PlaceDto.fromEntity(place)
            } else {
                null
            }
        }
    }

    fun getGroupDetail(groupId: Long): GroupDetailResponse {
        val userDto = ContextHolder.getUserInfoWithCheck()
        val userNo = userDto.userNo!!

        // 1. 그룹 존재 여부 확인
        val group: GroupEntity = groupRepository.findGroupWithDetailsById(groupId)
            ?: throw IllegalArgumentException("존재하지 않는 그룹입니다.")

        // 2. 사용자가 해당 그룹의 멤버인지 확인
        val userGroupId = UserGroupId(userNo, groupId)
        userGroupRepository.findById(userGroupId)
            ?: throw IllegalArgumentException("그룹에 속하지 않은 사용자입니다.")

        // 3. 그룹의 멤버 정보 구성
        val members = group.members.map { userGroup ->
            GroupMember(
                userNo = userGroup.userNo,
                isOwner = userGroup.isOwner,
                email = userGroup.user?.email ?: "",
                nickName = userGroup.user?.nickName ?: "",
                imgPath = userGroup.user?.imgPath
            )
        }

        // 4. 그룹의 장소 정보 조회
        val groupPlaces = groupPlaceRepository.findPlacesWithDetailsByGroupId(groupId)
        val places = groupPlaces.mapNotNull { groupPlace ->
            val place = groupPlace.place
            if (place != null) {
                PlaceDto.fromEntity(place)
            } else {
                null
            }
        }

        // 5. 응답 생성
        return GroupDetailResponse(
            groupId = group.groupId!!,
            groupName = group.groupName,
            regTime = group.regTime!!,
            modTime = group.modTime!!,
            members = members,
            memberCount = members.size,
            places = places,
            placeCount = places.size
        )
    }

    @Transactional
    fun leaveGroup(groupId: Long) {
        val userDto = ContextHolder.getUserInfoWithCheck()
        val userNo = userDto.userNo!!

        groupRepository.findById(groupId)
            ?: throw IllegalArgumentException("존재하지 않는 그룹입니다.")

        val userGroupId = UserGroupId(userNo, groupId)
        val userGroup: UserGroupEntity = userGroupRepository.findById(userGroupId)
            ?: throw IllegalArgumentException("그룹에 속하지 않은 사용자입니다.")

        if (userGroup.isOwner) {
            val nextOwner: UserGroupEntity? = userGroupRepository.findFirstNonOwnerByGroupId(groupId)

            if (nextOwner != null) {
                nextOwner.isOwner = true
                userGroupRepository.deleteById(userGroup)
            } else {
                userGroupRepository.deleteById(userGroup)
                groupPlaceRepository.deleteByGroupId(groupId)
                groupRepository.deleteById(groupId)
            }
        } else {
            userGroupRepository.deleteById(userGroup)
        }
    }
}
