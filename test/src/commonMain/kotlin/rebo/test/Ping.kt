package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.properties.PrimaryKey
import libetal.rebo.annotations.exposed.properties.Unique

@Entity
data class Ping(
    @Column
    val longitude: Long,
    @Column
    val latitude:Long,
    @Column
    val accuracy:Float,
    @Column
    @PrimaryKey
    val id: Int
)
