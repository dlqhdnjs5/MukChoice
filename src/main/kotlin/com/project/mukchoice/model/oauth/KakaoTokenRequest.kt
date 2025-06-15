package com.project.mukchoice.model.oauth

data class KakaoTokenRequest(
    val grant_type: String = "authorization_code",
    val client_id: String,
    val redirect_uri: String,
    val code: String,
    val client_secret: String? = null
)