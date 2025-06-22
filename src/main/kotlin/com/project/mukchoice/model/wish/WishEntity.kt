package com.project.mukchoice.model.wish

import com.project.mukchoice.model.place.PlaceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

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
    val regTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id", referencedColumnName = "id", insertable = false, updatable = false)
    val place: PlaceEntity? = null
)

data class WishId(
    var userNo: Int? = null,
    var placeId: Long? = null
) : Serializable