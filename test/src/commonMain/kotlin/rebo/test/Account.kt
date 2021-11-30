package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.properties.PrimaryKey

@Entity
data class Account(
    @Column
    val name: String
) {
    @Column
    @PrimaryKey
    var id: Int = 0
}


