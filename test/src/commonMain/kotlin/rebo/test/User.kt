package rebo.test

import libetal.rebo.annotations.exposed.columns.Column
import libetal.rebo.annotations.exposed.entities.Entity
import libetal.rebo.annotations.exposed.enums.ReferenceOption
import libetal.rebo.annotations.exposed.properties.ForeignKey
import libetal.rebo.annotations.exposed.properties.PrimaryKey
import libetal.rebo.annotations.exposed.properties.Unique

@Entity
data class User(
    @Column
    val name: String,

    @Column
    @Unique("name")
    @ForeignKey(onUpdate= ReferenceOption.NO_ACTION)
    var account: Account
){
    @Column
    @PrimaryKey
    var id:Int  = 0
}

