package com.project.mukchoice.repository

import com.project.mukchoice.model.place.PlaceEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class PlaceRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun findById(id: Long): PlaceEntity? {
        return entityManager.find(PlaceEntity::class.java, id)
    }

    fun save(entity: PlaceEntity): PlaceEntity {
        entityManager.persist(entity)
        return entity
    }
}