package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.enums.ReferenceOption
import libetal.rebo.annotations.exposed.properties.ForeignKey
import libetal.rebo.annotations.exposed.properties.PrimaryKey

@Entity
data class Group(
    @Column
    @PrimaryKey
    val id: Int = 0,
    @Column
    val name: String,
    @Column(default = "0")
    @ForeignKey(onDelete = ReferenceOption.CASCADE)
    val parent: Group? = null

)
