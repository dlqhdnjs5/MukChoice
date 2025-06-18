package com.project.mukchoice.model.wish

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.io.Serializable

@Entity
@Table(name = "wish")
@IdClass(WishId::class)
class WishEntity(
    @Id
    @Column(name = "user_no")
    val userNo: Int,

    @Id
    @Column(name = "place_id")
    val placeId: Long,

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, updatable = false)
    val regTime: LocalDateTime? = null
)

data class WishId(
    var userNo: Int? = null,
    var placeId: Long? = null
) : Serializable