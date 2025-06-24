package com.project.mukchoice.repository

import com.project.mukchoice.model.wish.WishEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class WishRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun save(entity: WishEntity): WishEntity {
        entityManager.persist(entity)
        return entity
    }

    fun existsByUserNoAndPlaceId(userNo: Int, placeId: Long): Boolean {
        val query = "SELECT COUNT(w) FROM WishEntity w WHERE w.userNo = :userNo AND w.placeId = :placeId"
        val count = entityManager.createQuery(query, java.lang.Long::class.java)
            .setParameter("userNo", userNo)
            .setParameter("placeId", placeId)
            .singleResult
        return count > 0
    }

    fun deleteByUserNoAndPlaceId(userNo: Int, placeId: Long) {
        val query = "DELETE FROM WishEntity w WHERE w.userNo = :userNo AND w.placeId = :placeId"
        entityManager.createQuery(query)
            .setParameter("userNo", userNo)
            .setParameter("placeId", placeId)
            .executeUpdate()
    }

    fun findByUserNoWithPaging(userNo: Int, offset: Int, limit: Int): List<WishEntity> {
        val query = "SELECT w FROM WishEntity w WHERE w.userNo = :userNo ORDER BY w.regTime DESC"
        return entityManager.createQuery(query, WishEntity::class.java)
            .setParameter("userNo", userNo)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    fun findByUserNoWithPagingWithPlace(userNo: Int, offset: Int, limit: Int): List<WishEntity> {
        val query = "SELECT w FROM WishEntity w WHERE w.userNo = :userNo ORDER BY w.regTime DESC"
        return entityManager.createQuery(query, WishEntity::class.java)
            .setParameter("userNo", userNo)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList
    }

    fun countByUserNo(userNo: Int): Long {
        val query = "SELECT COUNT(w) FROM WishEntity w WHERE w.userNo = :userNo"
        return entityManager.createQuery(query, java.lang.Long::class.java)
            .setParameter("userNo", userNo)
            .singleResult.toLong()
    }
}