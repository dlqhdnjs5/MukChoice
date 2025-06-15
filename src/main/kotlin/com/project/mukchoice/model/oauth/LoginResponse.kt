package com.project.mukchoice.model.oauth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val jwtToken: String
)