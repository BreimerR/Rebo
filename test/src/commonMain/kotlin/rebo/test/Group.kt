package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.properties.PrimaryKey

@Entity
data class Group(
    @Column
    @PrimaryKey
    val id: Int = 0,
    @Column
    val name: String,
    @Column
    val parent:Group? = null

)
