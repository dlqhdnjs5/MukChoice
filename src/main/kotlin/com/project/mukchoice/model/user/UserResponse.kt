package com.project.mukchoice.model.user

data class UserResponse(
    val userDTO: UserDto,
    val jwtToken: String
)
