package com.project.mukchoice.repository

import com.project.mukchoice.model.user.UserEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    @PersistenceContext private val entityManager: EntityManager
) {
    fun findUserById(userId: String): UserEntity? {
        return entityManager.find(UserEntity::class.java, userId)
    }

    fun findByEmail(email: String): UserEntity? {
        val jpql = "SELECT u FROM UserEntity u WHERE u.email = :email"
        val query = entityManager.createQuery(jpql, UserEntity::class.java)
        query.setParameter("email", email)
        return query.resultList.firstOrNull()
    }

    fun save(user: UserEntity) {
        entityManager.persist(user)
    }

    fun update(user: UserEntity) {
        entityManager.merge(user)
    }

    fun deleteUser(userId: String) {
        val user = findUserById(userId)
        if (user != null) {
            entityManager.remove(user)
        }
    }
}