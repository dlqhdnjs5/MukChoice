package com.project.mukchoice.model.group

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "`group`")
class GroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    val groupId: Long? = null,

    @Column(name = "group_name", nullable = false, length = 100)
    val groupName: String,

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false, updatable = false)
    val regTime: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "mod_time", nullable = false)
    var modTime: LocalDateTime? = null,

    @OneToMany(mappedBy = "groupId", fetch = FetchType.EAGER)
    val members: List<UserGroupEntity> = emptyList()
) {
    constructor(groupName: String) : this(
        groupId = null,
        groupName = groupName,
        regTime = null,
        modTime = null,
        members = emptyList()
    )
}
