package com.project.mukchoice.model.oauth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserResponse(
    val id: Long,
    val has_signed_up: Boolean? = null,
    val connected_at: String? = null,
    val synched_at: String? = null,
    val properties: Map<String, String>? = null,
    val kakao_account: KakaoAccount? = null,
    val for_partner: Map<String, Any>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoAccount(
    val profile_needs_agreement: Boolean? = null,
    val profile_nickname_needs_agreement: Boolean? = null,
    val profile_image_needs_agreement: Boolean? = null,
    val profile: Profile? = null,
    val name_needs_agreement: Boolean? = null,
    val name: String? = null,
    val email_needs_agreement: Boolean? = null,
    val is_email_valid: Boolean? = null,
    val is_email_verified: Boolean? = null,
    val email: String? = null,
    val age_range_needs_agreement: Boolean? = null,
    val age_range: String? = null,
    val birthyear_needs_agreement: Boolean? = null,
    val birthyear: String? = null,
    val birthday_needs_agreement: Boolean? = null,
    val birthday: String? = null,
    val birthday_type: String? = null,
    val is_leap_month: Boolean? = null,
    val gender_needs_agreement: Boolean? = null,
    val gender: String? = null,
    val phone_number_needs_agreement: Boolean? = null,
    val phone_number: String? = null,
    val ci_needs_agreement: Boolean? = null,
    val ci: String? = null,
    val ci_authenticated_at: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Profile(
    val nickname: String? = null,
    val thumbnail_image_url: String? = null,
    val profile_image_url: String? = null,
    val is_default_image: Boolean? = null,
    val is_default_nickname: Boolean? = null
)