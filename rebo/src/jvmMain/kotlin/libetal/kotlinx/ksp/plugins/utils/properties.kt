package libetal.kotlinx.ksp.plugins.utils

val primaryInitializers by lazy {
    mapOf(
        "String" to "varchar",
        "kotlin.String" to "varchar",
        "Int" to "integer",
        "kotlin.Int" to "integer",
        "UInt" to "uinteger",
        "kotlin.UInt" to "uinteger",
        "Long" to "long",
        "kotlin.Long" to "long",
        "Float" to "float",
        "kotlin.Float" to "float",
        "Double" to "double",
        "kotlin.Double" to "double",
    )
}

val primitiveInitializers by lazy {
    mapOf(
        "Boolean" to "bool",
        "kotlin.Boolean" to "bool",
        "Short" to "short",
        "kotlin.Short" to "short",
        "Byte" to "byte",
        "kotlin.Byte" to "byte",
        *(primaryInitializers.map { (a, b) -> a to b }.toTypedArray())
    )
}
