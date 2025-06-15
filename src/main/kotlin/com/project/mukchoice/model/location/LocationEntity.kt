package com.project.mukchoice.model.location

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "location")
class LocationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_no")
    val locationNo: Int? = null,

    @Column(name = "user_no", nullable = false)
    val userNo: Int,

    @Column(name = "address_name", nullable = false, length = 200)
    val addressName: String,

    @Column(name = "x", nullable = false)
    val x: Double,

    @Column(name = "y", nullable = false)
    val y: Double,

    @Column(name = "hcode")
    val hcode: String?,

    @Column(name = "bcode")
    val bcode: String?,

    @Column(name = "post_no")
    val postNo: String?,

    @Column(name = "sigungu")
    val sigungu: String?,

    @Column(name = "sido")
    val sido: String?,

    @Column(name = "dong")
    val dong: String?,

    @Column(name = "user_address")
    val userAddress: String? = null,

    @Column(name = "is_selected")
    var isSelected: Boolean,

    @Column(name = "reg_time", nullable = false)
    val regTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "mod_time", nullable = false)
    val modTime: LocalDateTime = LocalDateTime.now()
) {
    constructor(
        userNo: Int,
        addressName: String,
        x: Double,
        y: Double,
        hcode: String?,
        bcode: String?,
        postNo: String?,
        sigungu: String?,
        sido: String?,
        dong: String?,
        userAddress: String?,
        isSelected: Boolean
    ) : this(
        locationNo = null,
        userNo = userNo,
        addressName = addressName,
        x = x,
        y = y,
        hcode = hcode,
        bcode = bcode,
        postNo = postNo,
        sigungu = sigungu,
        sido = sido,
        dong = dong,
        userAddress = userAddress,
        isSelected = isSelected
    )
}