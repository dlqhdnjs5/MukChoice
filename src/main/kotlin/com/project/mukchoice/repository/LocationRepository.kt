package com.project.mukchoice.repository

import com.project.mukchoice.model.location.LocationEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class LocationRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun insertLocation(locationEntity: LocationEntity) {
        entityManager.persist(locationEntity)
    }

    fun updateLocation(locationEntity: LocationEntity) {
        entityManager.merge(locationEntity)
    }

    fun findLocationsByUserNo(userNo: Int): List<LocationEntity> {
        val jpql = "SELECT location FROM LocationEntity location WHERE location.userNo = :userNo"
        return entityManager.createQuery(jpql, LocationEntity::class.java)
            .setParameter("userNo", userNo)
            .resultList
    }

    fun findCurrentLocationsByUserNo(userNo: Int): LocationEntity? {
        val jpql = "SELECT location FROM LocationEntity location WHERE location.userNo = :userNo AND location.isSelected = true"
        return entityManager.createQuery(jpql, LocationEntity::class.java)
            .setParameter("userNo", userNo)
            .resultList.firstOrNull()
    }

    fun deleteLocation(locationNo: Int, userNo: Int) {
        val locationEntity: LocationEntity? = findLocationByNoAndUserNo(locationNo, userNo)
        check(locationEntity != null) { "Location not found or does not belong to the user." }

        entityManager.remove(locationEntity)
    }

    fun findLocationByNoAndUserNo(locationNo: Int, userNo: Int): LocationEntity? {
        val jpql = "SELECT location FROM LocationEntity location WHERE location.locationNo = :locationNo AND location.userNo = :userNo"
        return entityManager.createQuery(jpql, LocationEntity::class.java)
            .setParameter("locationNo", locationNo)
            .setParameter("userNo", userNo)
            .resultList.firstOrNull()
    }

    fun findLocationByLocationNo(locationNo: Int): LocationEntity? {
        val jpql = "SELECT location FROM LocationEntity location WHERE location.locationNo = :locationNo"
        return entityManager.createQuery(jpql, LocationEntity::class.java)
            .setParameter("locationNo", locationNo)
            .resultList.firstOrNull()
    }

}