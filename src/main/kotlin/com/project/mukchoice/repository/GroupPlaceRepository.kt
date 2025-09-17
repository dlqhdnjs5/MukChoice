package com.project.mukchoice.repository

import com.project.mukchoice.model.group.GroupPlaceEntity
import com.project.mukchoice.model.group.GroupPlaceId
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class GroupPlaceRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun findById(groupPlaceId: GroupPlaceId): GroupPlaceEntity? {
        return entityManager.find(GroupPlaceEntity::class.java, groupPlaceId)
    }

    fun save(entity: GroupPlaceEntity): GroupPlaceEntity {
        entityManager.persist(entity)
        return entity
    }

    fun findByGroupId(groupId: Long): List<GroupPlaceEntity> {
        val jpql = "SELECT gp FROM GroupPlaceEntity gp WHERE gp.groupId = :groupId"
        val query = entityManager.createQuery(jpql, GroupPlaceEntity::class.java)
        query.setParameter("groupId", groupId)
        return query.resultList
    }

    fun existsByGroupIdAndPlaceId(groupId: Long, placeId: Long): Boolean {
        val jpql = "SELECT COUNT(gp) FROM GroupPlaceEntity gp WHERE gp.groupId = :groupId AND gp.placeId = :placeId"
        val query = entityManager.createQuery(jpql, Long::class.java)
        query.setParameter("groupId", groupId)
        query.setParameter("placeId", placeId)
        return query.singleResult > 0
    }

    fun findPlacesWithDetailsByGroupId(groupId: Long): List<GroupPlaceEntity> {
        val jpql = """
            SELECT gp FROM GroupPlaceEntity gp 
            LEFT JOIN FETCH gp.place 
            WHERE gp.groupId = :groupId 
            ORDER BY gp.regTime DESC
        """
        val query = entityManager.createQuery(jpql, GroupPlaceEntity::class.java)
        query.setParameter("groupId", groupId)
        return query.resultList
    }

    fun deleteByGroupId(groupId: Long) {
        val jpql = "DELETE FROM GroupPlaceEntity gp WHERE gp.groupId = :groupId"
        val query = entityManager.createQuery(jpql)
        query.setParameter("groupId", groupId)
        query.executeUpdate()
    }
}
