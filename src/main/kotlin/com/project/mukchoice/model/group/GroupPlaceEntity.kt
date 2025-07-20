package com.project.mukchoice.model.group

import com.project.mukchoice.model.place.PlaceEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "group_place")
@IdClass(GroupPlaceId::class)
class GroupPlaceEntity(
    @Id
    @Column(name = "group_id", nullable = false)
    val groupId: Long,

    @Id
    @Column(name = "place_id", nullable = false)
    val placeId: Long,

    @Column(name = "register", nullable = false)
    val register: Int,

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, updatable = false)
    val regTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(
        name = "place_id", referencedColumnName = "id", insertable = false, updatable = false,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    val place: PlaceEntity? = null
)

data class GroupPlaceId(
    var groupId: Long? = null,
    var placeId: Long? = null
) : Serializable

