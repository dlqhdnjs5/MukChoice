package com.project.mukchoice.util

import com.project.mukchoice.model.user.UserDto

class ContextHolder {
    companion object {
        private var userDtoInfoThreadLocal = ThreadLocal<UserDto>()

        private fun getUser(): UserDto {
            return userDtoInfoThreadLocal.get()
        }

        fun putUser(userDTO: UserDto) {
            userDtoInfoThreadLocal.set(userDTO)
        }

        fun clearUser() {
            userDtoInfoThreadLocal.remove()
        }

        fun getUserInfoWithCheck(): UserDto {
            return try {
                getUser()
            } catch (e: NullPointerException) {
                throw IllegalStateException("UserDto is not set in ThreadLocal", e)
            }
        }
    }
}