package com.project.mukchoice.repository

import com.project.mukchoice.model.group.GroupInvitationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupInvitationRepository : JpaRepository<GroupInvitationEntity, String> {

    fun findByGroupIdAndInviterUserNo(groupId: Long, inviterUserNo: Int): List<GroupInvitationEntity>

    fun findByGroupId(groupId: Long): List<GroupInvitationEntity>
}
