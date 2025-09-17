package com.project.mukchoice.repository

import com.project.mukchoice.model.group.UserGroupEntity
import com.project.mukchoice.model.group.UserGroupId
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class UserGroupRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun findById(userGroupId: UserGroupId): UserGroupEntity? {
        return entityManager.find(UserGroupEntity::class.java, userGroupId)
    }

    fun save(entity: UserGroupEntity): UserGroupEntity {
        entityManager.persist(entity)
        return entity
    }

    fun findByUserNo(userNo: Int): List<UserGroupEntity> {
        val jpql = "SELECT ug FROM UserGroupEntity ug WHERE ug.userNo = :userNo"
        val query = entityManager.createQuery(jpql, UserGroupEntity::class.java)
        query.setParameter("userNo", userNo)
        return query.resultList
    }

    fun findByGroupId(groupId: Long): List<UserGroupEntity> {
        val jpql = "SELECT ug FROM UserGroupEntity ug WHERE ug.groupId = :groupId"
        val query = entityManager.createQuery(jpql, UserGroupEntity::class.java)
        query.setParameter("groupId", groupId)
        return query.resultList
    }

    fun findOwnerByGroupId(groupId: Long): UserGroupEntity? {
        val jpql = "SELECT ug FROM UserGroupEntity ug WHERE ug.groupId = :groupId AND ug.isOwner = true"
        val query = entityManager.createQuery(jpql, UserGroupEntity::class.java)
        query.setParameter("groupId", groupId)
        return query.resultList.firstOrNull()
    }

    fun deleteById(userGroupEntity: UserGroupEntity) {
        entityManager.remove(userGroupEntity)
    }

    fun findFirstNonOwnerByGroupId(groupId: Long): UserGroupEntity? {
        val jpql = "SELECT ug FROM UserGroupEntity ug WHERE ug.groupId = :groupId AND ug.isOwner = false"
        val query = entityManager.createQuery(jpql, UserGroupEntity::class.java)
        query.maxResults = 1
        query.setParameter("groupId", groupId)
        return query.resultList.firstOrNull()
    }
}
