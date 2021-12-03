package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.properties.PrimaryKey


enum class Country(
    @Column
    val identifier: String,
    @Column
    @PrimaryKey val id: Int
) {
    KENYA("Kanye", 1)
}