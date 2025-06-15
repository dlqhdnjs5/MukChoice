package com.project.mukchoice.service

import com.project.mukchoice.model.user.UserEntity
import com.project.mukchoice.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
) {
    fun getUserByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    fun createUser(user: UserEntity) {
        return userRepository.save(user)
    }

}