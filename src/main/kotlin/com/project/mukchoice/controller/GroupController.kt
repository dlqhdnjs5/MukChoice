package com.project.mukchoice.controller

import com.project.mukchoice.facade.GroupFacade
import com.project.mukchoice.model.group.CreateGroupRequest
import com.project.mukchoice.model.group.GroupResponse
import com.project.mukchoice.model.group.AddPlaceToGroupRequest
import com.project.mukchoice.model.group.GroupListResponse
import com.project.mukchoice.model.group.GroupDetailResponse
import com.project.mukchoice.model.group.CreateGroupInvitationRequest
import com.project.mukchoice.model.group.GroupInvitationResponse
import com.project.mukchoice.service.GroupService
import com.project.mukchoice.service.GroupInvitationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupService: GroupService,
    private val groupFacade: GroupFacade,
    private val groupInvitationService: GroupInvitationService
) {

    /**
     * 새로운 그룹을 생성하는 API
     * @param userNo 그룹을 생성하는 사용자 번호 (헤더에서 받아옴)
     * @param request 그룹 생성 요청 (그룹명 포함)
     * @return GroupResponse 생성된 그룹 정보
     */
    @PostMapping
    fun createGroup(
        @RequestBody request: CreateGroupRequest
    ): GroupResponse {
        return groupService.createGroup(request)
    }

    /**
     * 그룹에 장소를 추가하는 API
     * @param groupId 장소를 추가할 그룹 ID
     * @param request 추가할 장소 ID가 포함된 요청
     * @return GroupPlaceResponse 추가된 장소 정보
     */
    @PostMapping("/{groupId}/places")
    fun addPlaceToGroup(
        @PathVariable groupId: Long,
        @RequestBody request: AddPlaceToGroupRequest
    ) {
        groupFacade.addPlaceToGroup(groupId, request)
    }

    /**
     * 사용자가 속한 그룹 목록을 조회하는 API
     * 각 그룹에 속한 멤버 정보도 함께 조회
     * @return List<GroupListResponse> 그룹 목록과 멤버 정보
     */
    @GetMapping
    fun getGroupInfoList(): List<GroupListResponse> {
        return groupService.getGroupInfoList()
    }

    /**
     * 특정 그룹의 상세 정보를 조회하는 API
     * 그룹 정보, 멤버 목록, 장소 목록을 모두 포함
     * @param groupId 조회할 그룹 ID
     * @return GroupDetailResponse 그룹 상세 정보
     */
    @GetMapping("/{groupId}")
    fun getGroupDetail(
        @PathVariable groupId: Long
    ): GroupDetailResponse {
        return groupFacade.getGroupDetail(groupId)
    }

    /**
     * 그룹 초대를 생성하는 API
     * @param request 그룹 초대 요청 정보
     * @return GroupInvitationResponse 생성된 초대 정보
     */
    @PostMapping("/invitations")
    fun createGroupInvitation(
        @RequestBody request: CreateGroupInvitationRequest
    ): GroupInvitationResponse {
        return groupInvitationService.createInvitation(request)
    }

    /**
     * 특정 그룹의 초대 목록을 조회하는 API
     * @param groupId 그룹 ID
     * @return List<GroupInvitationResponse> 해당 그룹의 초대 목록
     */
    @GetMapping("/{groupId}/invitations")
    fun getGroupInvitations(
        @PathVariable groupId: Long
    ): List<GroupInvitationResponse> {
        return groupInvitationService.getInvitationsByGroupId(groupId)
    }
}


