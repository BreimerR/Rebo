package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.entities.NoUpdateProperties
import libetal.rebo.annotations.exposed.properties.PrimaryKey

@Entity
@NoUpdateProperties
data class Account(
    @Column
    val name: String
) {
    @Column
    @PrimaryKey
    var id: Int = 0
}


