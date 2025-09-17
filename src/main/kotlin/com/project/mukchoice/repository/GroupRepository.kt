package com.project.mukchoice.repository

import com.project.mukchoice.model.group.GroupEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class GroupRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun findById(groupId: Long): GroupEntity? {
        return entityManager.find(GroupEntity::class.java, groupId)
    }

    fun save(entity: GroupEntity): GroupEntity {
        entityManager.persist(entity)
        return entity
    }

    fun findByGroupName(groupName: String): GroupEntity? {
        val jpql = "SELECT g FROM GroupEntity g WHERE g.groupName = :groupName"
        val query = entityManager.createQuery(jpql, GroupEntity::class.java)
        query.setParameter("groupName", groupName)
        return query.resultList.firstOrNull()
    }

    fun findGroupsByUserNo(userNo: Int): List<GroupEntity> {
        val jpql = """
            SELECT g FROM GroupEntity g 
            JOIN UserGroupEntity ug ON g.groupId = ug.groupId 
            WHERE ug.userNo = :userNo
            ORDER BY g.regTime DESC
        """
        val query = entityManager.createQuery(jpql, GroupEntity::class.java)
        query.setParameter("userNo", userNo)
        return query.resultList
    }

    fun countGroupsByOwner(userNo: Int): Long {
        val jpql = """
            SELECT COUNT(g) FROM GroupEntity g 
            JOIN UserGroupEntity ug ON g.groupId = ug.groupId 
            WHERE ug.userNo = :userNo AND ug.isOwner = true
        """
        val query = entityManager.createQuery(jpql, Long::class.java)
        query.setParameter("userNo", userNo)
        return query.singleResult
    }

    fun countPlacesByGroupIds(groupIds: List<Long>): Map<Long, Long> {
        if (groupIds.isEmpty()) return emptyMap()

        val jpql = """
            SELECT gp.groupId, COUNT(gp.placeId) 
            FROM GroupPlaceEntity gp 
            WHERE gp.groupId IN :groupIds 
            GROUP BY gp.groupId
        """
        val query = entityManager.createQuery(jpql)
        query.setParameter("groupIds", groupIds)

        val results = query.resultList as List<Array<Any>>
        return results.associate {
            (it[0] as Long) to (it[1] as Long)
        }
    }

    fun findGroupWithDetailsById(groupId: Long): GroupEntity? {
        val jpql = """
            SELECT g FROM GroupEntity g 
            INNER JOIN FETCH g.members 
            WHERE g.groupId = :groupId
        """
        val query = entityManager.createQuery(jpql, GroupEntity::class.java)
        query.setParameter("groupId", groupId)
        return query.resultList.firstOrNull()
    }

    fun deleteById(groupId: Long) {
        val entity = findById(groupId)
        if (entity != null) {
            entityManager.remove(entity)
        }
    }
}
