package com.project.mukchoice.model.place

import com.project.mukchoice.consts.PlaceCategory
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "place")
class PlaceEntity(
    @Id
    @Column(name = "id")
    var id: Long,

    @Column(name = "place_name", length = 100, nullable = false)
    var placeName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "place_category", length = 100, nullable = false)
    val placeCategory: PlaceCategory,

    @Column(length = 50)
    val phone: String? = null,

    @Column(name = "address_name", length = 255)
    val addressName: String? = null,

    @Column(name = "road_address_name", length = 255)
    val roadAddressName: String? = null,

    @Column(nullable = false)
    val x: Double,

    @Column(nullable = false)
    val y: Double,

    @Column(name = "place_url", length = 500, nullable = false)
    val placeUrl: String,

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, updatable = false)
    val regTime: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "mod_time", nullable = false)
    val modTime: LocalDateTime? = null,
)