package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.enums.ReferenceOption
import libetal.rebo.annotations.exposed.properties.ForeignKey
import libetal.rebo.annotations.exposed.properties.PrimaryKey

@Entity
data class AccountGroup(
    @Column
    @ForeignKey(safeInsert = true)
    val group: Group,

    @Column
    @ForeignKey(safeInsert = true)
    val account: Account

) {
    @Column
    @PrimaryKey
    var id: Int = 0
}