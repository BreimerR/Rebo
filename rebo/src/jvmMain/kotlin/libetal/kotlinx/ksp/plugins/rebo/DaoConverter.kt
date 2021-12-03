package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.strings.isBlank
import kotlinx.strings.lcFirst
import kotlinx.strings.ucFirst
import libetal.kotlinx.ksp.plugins.utils.Converter

class DaoConverter(override var declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {

    private var indent = ""

    private val tableQualifiedName by lazy {
        declaration.tableQualifiedName
    }
    private val daoName by lazy {
        declaration.daoName
    }

    private val daoFqName by lazy {
        declaration.daoQualifiedName
    }

    private val daoColumns by lazy {
        var code = ""

        declaration.columns.forEach { column ->
            with(column) {
                val rightHandExpression = if (!isForeign) {
                    "$tableQualifiedName.$propertyName"
                } else {
                    val function = if (isNullable) "optionalReferencedOn" else "referencedOn"

                    daoClass?.let { daoClass ->
                        "${daoClass.daoQualifiedName} $function $tableQualifiedName.$propertyName"
                    } ?: throw RuntimeException("Can't find daoClass for $fqName : $qualifiedReturnType")
                }

                var variableType = if (isForeign) {
                    ": $daoFqName"
                } else if (isPrimary) {
                    ""
                } else {
                    ""
                }

                if (isNullable) variableType += "?"

                if (!(isPrimary && propertyName == "id")) {
                    code += """|
                    |${indent}var $propertyName$variableType by $rightHandExpression
                """.trimMargin()
                }
            }

        }

        code
    }

    private val fqName by lazy {
        declaration.fqName
    }

    private val simpleName by lazy {
        declaration.simpleName.asString()
    }

    private val primaryColumn by lazy {
        declaration.primaryColumn
    }

    private val primaryColumnPrimitiveType by lazy {
        primaryColumn?.primitiveTypeSimpleString
    }

    private val argName by lazy {
        simpleName.lcFirst
    }

    private val invokeMethod by lazy {

        var code = """|
            |${indent}operator fun invoke(): $fqName = transaction {""".trimMargin()

        if (declaration.classKind == ClassKind.CLASS) {
            val constructorStart = """|
                |$indent    $fqName(""".trimMargin()

            code += constructorStart

            val constructorCode = mutableListOf<String>()
            val nonConstructorCode = mutableListOf<String>()

            with(declaration) {
                columns.forEach { column ->
                    val propName = column.columnName
                    with(column) {
                        val rightHandExpression = if (isForeign) {
                            var result = propertyName

                            result += if (isNullable) "?"
                            else ""
                            "$result.invoke()"
                        } else {
                            if (isPrimary) {
                                "this@$daoName.$propertyName.value"
                            } else """this@$daoName.${propName}"""
                        }

                        val savedIndent = indent
                        indent += "    "

                        if (inConstructor) {
                            constructorCode += """$propertyName = $rightHandExpression"""
                        } else {
                            nonConstructorCode += """this.$propertyName = $rightHandExpression"""
                        }

                        indent = savedIndent
                    }
                }
            }

            code += if (constructorCode.isEmpty()) ")"
            else {
                val parameters = constructorCode.joinToString(
                    """|,
                       |$indent        
                    """.trimMargin()
                )
                """|
                   |$indent        $parameters
                   |$indent    )""".trimMargin()
            }

            code += if (nonConstructorCode.isNotEmpty()) {
                val savedIndent = indent
                indent += "    "
                val nonConstructorInitializers = nonConstructorCode.joinToString(
                    """|
                       |$indent    
                    """.trimMargin()
                )

                val result = """|.apply{
                   |$indent    $nonConstructorInitializers
                   |$indent}""".trimMargin()

                indent = savedIndent
                result
            } else ""

        }

        code += """|
            |$indent}""".trimMargin()

        code

    }

    private val newBody by lazy {
        val code = declaration.columns.map {
            with(it) {
                if (isForeign) {
                    if (isNullable) """|
                           |$indent    $argName.$propertyName?.let {
                           |$indent        $propertyName = $daoFqName.${if (safeInsert) "firstOrNew" else "new"}(it)
                           |$indent    }
                        """.trimMargin()
                    else """|
                            |$indent    $propertyName = $daoFqName.${if (safeInsert) "firstOrNew" else "new"}($argName.$propertyName)
                        """.trimMargin()
                } else {
                    if (!isPrimary) {
                        if (isNullable) {
                            """TODO("Contribute to Rebo source. Feature not supported DaoConverter[185]")"""
                        } else {
                            """|
                           |$indent    $propertyName = $argName.$propertyName
                        """.trimMargin()
                        }
                    } else ""
                }
            }
        }
        """|${indent}new {${code.joinToString("")}
           |$indent}""".trimMargin()
    }

    private val columns by lazy {
        declaration.columns
    }


    private val columnsAsParameters by lazy {
        columns.joinToString(", ") { column ->
            """${column.propertyName}: ${column.qualifiedReturnType}${if (column.isNullable) "?" else ""}"""
        }
    }

    private val KPropertyDeclaration.primaryKeyPrimitivePath
        get(): String {
            var result = propertyName

            if (isForeign) {
                result += (if (isNullable) "?" else "") + "." + immediateReferenced?.columns?.firstOrNull { it.propertyName == referencedFiledName || it.isPrimary }
                    ?.primaryKeyPrimitivePath
            }

            return result
        }

    private val firstQueries by lazy {
        val (nonUniques, uniqueOnes) = columns.partition { it.uniqueOn.isBlank }


        val pairUniques = mutableMapOf<KPropertyDeclaration, KPropertyDeclaration>()
        val unreferenced = mutableListOf<KPropertyDeclaration>()

        for (uniqueOne in uniqueOnes) {

            var referenced: KPropertyDeclaration? = null

            for (nonUnique in nonUniques) {
                if (nonUnique.propertyName == uniqueOne.uniqueOn) {
                    referenced = nonUnique
                } else unreferenced.add(nonUnique)
            }

            pairUniques[uniqueOne] = checkNotNull(referenced) {
                "Unique column ${uniqueOne.fqName} References a nonExisting property ${uniqueOne.pkgName}.${uniqueOne.uniqueOn}"
            }

        }

        val uniqueReferencedCode = pairUniques.map { (referee, reference) ->
            """(($tableQualifiedName.${referee.propertyName} eq ${referee.propertyName}) and ($tableQualifiedName.${referee.propertyName} eq ${reference.propertyName}))"""
        }

        val unReferencedCode = (if (unreferenced.isEmpty()) nonUniques else unreferenced).map { column ->
            """($tableQualifiedName.${column.propertyName} eq ${column.primaryKeyPrimitivePath})"""
        }

        (uniqueReferencedCode + unReferencedCode).joinToString(
            """| or
            |$indent$indent$indent$indent$indent$indent$indent
        """.trimMargin()
        ) {
            """|$it
            """.trimMargin()
        }

    }

    private fun KClassDeclaration.columnsAsStringValueConcat(vararg path: String = arrayOf(argName)): String =
        columns.filter { !(it.isForeign && fqName != null && fqName == it.immediateReferenced?.fqName) }.joinToString(", ") {
            if (it.isForeign) {
                it.immediateReferenced?.columnsAsStringValueConcat(argName, it.propertyName) ?: ""
            } else "$fqName($" + "{${path.joinToString(".")}.${it.propertyName}})"
        }

    private val columnsAsArguments by lazy {
        columns.joinToString(", ") {
            "$argName.${it.propertyName}"
        }
    }

    private fun KPropertyDeclaration.updateByPrimaryKeyString(primaryColumnName: String?): String {
        val spacing = "$indent            "
        val nullableName = "$argName${propertyName.ucFirst}"
        val argumentName = "$argName.$propertyName"

        val result = if (isPrimary && propertyName == "id")
            """|
                |$spacing// Property $propertyName is primary and can't be set 
                |$spacing// $propertyName  = $argumentName
            """.trimMargin()
        else if (isForeign) {
            if (isNullable) {
                """|
                    |$spacing$argumentName?.let { $nullableName ->
                    |$spacing    $propertyName = $daoFqName.update($argumentName.$primaryColumnName, $nullableName)
                    |$spacing}
                """.trimMargin()
            } else """|
                    |$spacing$propertyName = $daoFqName.update($argumentName.$primaryColumnName, $argumentName)
                """.trimMargin()
        } else {
            """|
                |$spacing$propertyName = $argumentName""".trimMargin()

        }

        return result
    }

    private val KPropertyDeclaration.updateString: String
        get(): String {

            val spacing = "$indent                "
            val nullableName = "$argName${propertyName.ucFirst}"
            val argumentName = "$argName.$propertyName"

            val result = if (isPrimary && propertyName == "id")
                """|
                |$spacing// Property $propertyName is primary and can't be set 
                |$spacing// $propertyName  = $argumentName
            """.trimMargin()
            else if (isForeign) {
                if (isNullable) {
                    """|
                    |$spacing$argumentName?.let { $nullableName ->
                    |$spacing    $propertyName = $daoFqName.update($nullableName)
                    |$spacing}
                """.trimMargin()
                } else """|
                    |$spacing$propertyName = $daoFqName.update($argumentName)
                """.trimMargin()
            } else {
                """|
                |$spacing$propertyName = $argumentName""".trimMargin()

            }

            return result

        }

    private val KClassDeclaration.updateByPrimaryKeyTransactionBody
        get(): String = columns.joinToString("") {
            it.updateByPrimaryKeyString(primaryColumn?.primaryKeyPrimitivePath)
        }

    private val KClassDeclaration.updateMethodTransactionBody
        get(): String =
            columns.joinToString("") {
                it.updateString
            }


    /**
     * fun update(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName) : EntityClassFullyQualifiedName =
     *     transaction{
     *         find(entityClassSimpleNameToCamelCase)?.apply{
     *              property = newPropertyValue
     *         } ?: throw java.sql.BatchUpdateException(Exception)
     *     }
     *
     * */
    private val updateMethod by lazy {
        """|
           |$indent    fun update($argName: $fqName): $daoFqName =
           |$indent        transaction {
           |$indent            find($argName)?.apply {${declaration.updateMethodTransactionBody}
           |$indent            }
           |$indent                ?: throw java.sql.BatchUpdateException(Exception("Passed $argName: $fqName doesn't exist on database with values ${
            declaration.columnsAsStringValueConcat(
                argName
            )
        }"))
           |$indent        }
        """.trimMargin()
    }


    /**
     * fun update(primaryKeyPropertyName: PrimaryKeyType, entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): EntityClassFullyQualifiedName = transaction {
     *     findById(int)?.apply {
     *         propertiesChangesSpread
     *     }
     *         ?: throw RuntimeException("Database doesn't have groups_table.id=${int}")
     * }
     * */
    private val updateByIdMethod by lazy {
        val primaryKeyName = primaryColumn?.propertyName
        val primaryKeyFqName = primaryColumn?.fqName
        val primaryColumnType = primaryColumn?.qualifiedReturnType
        val fqConcatProperty = "{$argName.${primaryColumn?.primaryKeyPrimitivePath}}"
        """|
            |$indent    fun update($primaryKeyName: ${primaryColumnType}, $argName: $fqName): $daoFqName = transaction {
            |$indent        findById(${primaryColumn?.primaryKeyPrimitivePath})?.apply {${declaration.updateByPrimaryKeyTransactionBody}
            |$indent        }
            |$indent            ?: throw java.sql.BatchUpdateException(Exception("Can't find row to update with primaryKey $primaryKeyFqName($$fqConcatProperty)"))
            |$indent    }
        """.trimMargin()
    }

    /**
     * fun first(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): EntityClassFullyQualifiedName? =
     *      first(entityClassAnnotatedSpreadProperties)?.invoke()
     */
    private val dataClassFirstMethod by lazy {
        """|
           |${indent}    fun first($argName: $fqName): $fqName? = 
           |$indent        first($columnsAsArguments)?.invoke()
        """.trimMargin()
    }

    private val parametrisedFirstMethod by lazy {
        val indent = "$indent    "
        """|
           |${indent}fun first($columnsAsParameters): $daoFqName? = 
           |$indent    transaction {
           |$indent        find {
           |$indent            $firstQueries
           |$indent        }.firstOrNull()
           |${indent}    }
        """.trimMargin()
    }

    /**
     * fun firstOrNew(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): EntityClassFullyQualifiedName =
     *     first(spreadProperties) ?: new(subjectLevel)
     * */
    private val firstOrNewMethod by lazy {
        """|
           |${indent}    fun firstOrNew($argName: $fqName): $daoFqName =
           |$indent        first($columnsAsArguments) ?: new($argName)
        """.trimMargin()
    }

    /**
     * fun firstOrInsert(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): EntityClassFullyQualifiedName =
     *     firstOrNew(subjectLevel)()
     * */
    private val firstOrInsert by lazy {
        """|
           |$indent    fun firstOrInsert($argName: $fqName): $fqName = firstOrNew($argName)()
        """.trimMargin()
    }

    /**
     *fun find(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): DaoClass? =
     *   first(spreadProperties)
     * */
    private val findEntityClassMethod by lazy {
        """|
           |$indent    fun find($argName: $fqName): $daoFqName? =
           |$indent        first($columnsAsArguments)
        """.trimMargin()
    }

    /**
     * public operator fun contains(entityClassSimpleNameToCamelCase: EntityClassFullyQualifiedName): Boolean = first(entityClassSimpleNameToCamelCase)?.let{true}  ?: false
     * */
    private val containsMethod by lazy {
        """|
           |$indent    operator fun contains($argName: $fqName): Boolean =
           |$indent        first($argName)?.let { true } ?: false
        """.trimMargin()
    }

    private val findByIdMethod by lazy {
        """|
           |$indent    fun find(id: $primaryColumnPrimitiveType): $fqName? = transaction {
           |$indent        findById(id)?.invoke()
           |$indent    }
        """.trimMargin()
    }

    private val insertMethod by lazy {
        """|
           |${indent}    fun insert($argName: $fqName): $fqName = new($argName)()
        """.trimMargin()
    }

    private val newMethod by lazy {
        val savedIndent = indent
        indent += "        "
        val result = """|
            |${savedIndent}    fun new($argName: $fqName): $daoName = transaction {
            |$newBody
            |$savedIndent    }""".trimMargin()
        indent = savedIndent
        result
    }

    private val allProperty by lazy {
        val savedIndent = indent
        indent += "    "

        val result = """|    val all: List<${declaration.fqName}>
            |$indent    get() = transaction {
            |$indent        all().map {
            |$indent            it()
            |$indent        }
            |$indent    }
        """.trimMargin()

        indent = savedIndent

        result
    }

    override fun convert(): String = with(declaration) {
        convert()
    }

    private fun KClassDeclaration.convert(): String {


        return if (hasPrimaryKey) {

            indent = "    "

            addImport("org.jetbrains.exposed.dao.Entity")
            addImport("org.jetbrains.exposed.dao.EntityClass")
            addImport("org.jetbrains.exposed.dao.id.EntityID")
            addImport("org.jetbrains.exposed.sql.or")
            addImport("org.jetbrains.exposed.sql.transactions.transaction")

            """|
            |class $daoName(id: $primaryEntityKeyTypeString) : $daoSuperClassWithId(id) {
            |$daoColumns
            |$invokeMethod
            |
            |${indent}companion object : EntityClass<Int, $daoName>($tableQualifiedName) {
            |$indent$allProperty
            |$containsMethod
            |$findByIdMethod
            |$findEntityClassMethod
            |$parametrisedFirstMethod
            |$dataClassFirstMethod
            |$firstOrNewMethod
            |$firstOrInsert
            |$insertMethod
            |$newMethod
            |$updateMethod
            |$updateByIdMethod
            |$indent}
            |}
        """.trimMargin()
        } else {
            /**
             * This feature isn't supported yet since exposed database does
             * not support for tables without primaryKeys enabled.
             *$newMethod
             * */
            addImport("org.jetbrains.exposed.sql.transactions.transaction")
            """|object $daoName : org.jetbrains.exposed.sql.Table(){
            |$columnsCode
            |}
        """.trimMargin()
        }
    }

}