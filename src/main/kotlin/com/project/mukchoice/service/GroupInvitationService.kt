package com.project.mukchoice.service

import com.project.mukchoice.model.group.CreateGroupInvitationRequest
import com.project.mukchoice.model.group.GroupInvitationEntity
import com.project.mukchoice.model.group.GroupInvitationResponse
import com.project.mukchoice.model.group.UserGroupEntity
import com.project.mukchoice.model.group.UserGroupId
import com.project.mukchoice.repository.GroupInvitationRepository
import com.project.mukchoice.repository.UserGroupRepository
import com.project.mukchoice.util.ContextHolder
import com.project.mukchoice.util.IdGenerator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class GroupInvitationService(
    private val groupInvitationRepository: GroupInvitationRepository,
    private val userGroupRepository: UserGroupRepository
) {

    @Transactional
    fun createInvitation(request: CreateGroupInvitationRequest): GroupInvitationResponse {
        val invitationId = IdGenerator.generateId()
        val userNo = ContextHolder.getUserInfoWithCheck().userNo

        val invitation = GroupInvitationEntity(
            id = invitationId,
            inviterUserNo = userNo!!,
            groupId = request.groupId,
            status = false
        )

        val savedInvitation = groupInvitationRepository.save(invitation)

        return GroupInvitationResponse(
            id = savedInvitation.id,
            inviterUserNo = savedInvitation.inviterUserNo,
            groupId = savedInvitation.groupId,
            status = savedInvitation.status,
            regTime = savedInvitation.regTime,
            modTime = savedInvitation.modTime
        )
    }

    fun getInvitationsByGroupId(groupId: Long): List<GroupInvitationResponse> {
        return groupInvitationRepository.findByGroupId(groupId).map { invitation ->
            GroupInvitationResponse(
                id = invitation.id,
                inviterUserNo = invitation.inviterUserNo,
                groupId = invitation.groupId,
                status = invitation.status,
                regTime = invitation.regTime,
                modTime = invitation.modTime
            )
        }
    }

    @Transactional
    fun acceptInvitationGroup(invitationId: String, userNo: Int) {
        val invitation = groupInvitationRepository.findById(invitationId)
            .orElseThrow { IllegalArgumentException("그룹 초대를 찾을 수 없습니다. ID: $invitationId") }

        val userGroupId = UserGroupId(
            userNo = userNo,
            groupId = invitation.groupId
        )

        val existingMember = userGroupRepository.findById(userGroupId)
        if (existingMember == null) {
            val userGroupEntity = UserGroupEntity(
                userNo = userNo,
                groupId = invitation.groupId
            )
            userGroupRepository.save(userGroupEntity)

            invitation.status = true
        }
    }
}

