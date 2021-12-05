package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.libetal.lazy.contexed.contexedLazy
import com.libetal.lazy.mutable.mutableLazy
import kotlinx.strings.isBlank
import libetal.kotlinx.ksp.plugins.rebo.Annotations.Column
import libetal.kotlinx.ksp.plugins.rebo.Annotations.ForeignKey
import libetal.kotlinx.ksp.plugins.rebo.Annotations.PrimaryKey
import libetal.kotlinx.ksp.plugins.utils.*
import libetal.rebo.annotations.exposed.enums.ReferenceOption


class KPropertyDeclaration(delegate: KSPropertyDeclaration, val entityClass: KClassDeclaration) :
    PropertyDeclaration<KPropertyDeclaration, KPropertyDeclaration>(delegate) {

    private var columnAnnotation: KSAnnotation by mutableLazy {
        annotations[Column] ?: throw RuntimeException("Registered a property that's not annotated by @$Column")
    }

    private var primaryAnnotation: KSAnnotation? by mutableLazy {
        annotations[PrimaryKey]
    }

    private var foreignAnnotation: KSAnnotation? by mutableLazy {
        annotations[ForeignKey]
    }

    var columnAnnotationArguments by mutableLazy {
        DelegateParameters(columnAnnotation.arguments)
    }

    private val foreignAnnotationArguments by mutableLazy {
        foreignAnnotation?.let {
            DelegateParameters(it.arguments)
        }
    }

    private var hasPrimaryAnnotation by mutableLazy {
        primaryAnnotation != null
    }


    private val isPrimitive by lazy {
        fqReturnType in primitiveInitializers
    }

    var defaultValue by mutableLazy {
        val default by columnAnnotationArguments {
            it?.toString()?.ifBlank { null } ?: ""
        }

        default
    }

    private val size by columnAnnotationArguments {
        it as? Int ?: run {
            when (fqReturnType) {
                "kotlinx.String" -> 255
                else -> 0
            }
        }
    }

    private val name by contexedLazy {
        foreignAnnotationArguments?.let {
            val name by it {
                it.toString().ifBlank { null }
            }

            name
        }
    }

    private val collate by columnAnnotationArguments {
        it?.toString()?.ifBlank { null }
    }

    val uniqueOn by columnAnnotationArguments {
        it?.toString()?.ifBlank { null }
    }
    private val unique by columnAnnotationArguments {
        it as? Boolean ?: false
    }

    private val primary by columnAnnotationArguments {
        it as? Boolean ?: false
    }

    var isPrimary by mutableLazy {
        hasPrimaryAnnotation || primary
    }

    private val isUnique by mutableLazy {
        unique || isPrimary
    }


    val referencedFiledName by mutableLazy {
        foreignAnnotationArguments?.let {
            val referencedFiledName by it {
                it.toString().ifBlank { null }
            }

            referencedFiledName
        }
    }

    private val onDelete by mutableLazy {
        foreignAnnotation?.arguments?.let { args ->
            args["onDelete"]?.value as? ReferenceOption
        }
    }

    private val onUpdate by mutableLazy {
        foreignAnnotation?.arguments?.let { args ->
            args["onUpdate"]?.value as? ReferenceOption
        }
    }
    val safeInsert by mutableLazy {
        foreignAnnotation?.arguments?.let { args ->
            args["safeInsert"]?.value?.toString()?.toBoolean()
        } ?: false
    }

    private val eagerLoading by columnAnnotationArguments {
        it as? Boolean ?: false
    }

    var propertyName by mutableLazy {
        simpleName.asString()
    }

    var columnName by mutableLazy {
        columnAnnotation.arguments["name"]?.value?.let {
            it.toString().ifBlank { null }
        } ?: propertyName
    }

    private val isAutoIncrement by columnAnnotationArguments {
        val autoIncrement = it as? Boolean ?: isPrimary

/*        check(autoIncrement && (qualifiedReturnType == "Int" || qualifiedReturnType == "kotlin.Int")) {
            "Can't have non Integer value of($qualifiedReturnType) as autoincrement"
        }*/

        autoIncrement

    }

    private val resolvedTypeDeclarationQualifiedName by lazy {
        resolvedType.declaration.qualifiedName?.asString()
    }

    val fqReturnType by lazy {
        checkNotNull(resolvedTypeDeclarationQualifiedName) {
            "Can't resolve return type of property ${qualifiedName?.asString()}"
        }
    }

    @Deprecated("Naming convention changed", ReplaceWith("fqReturnType"))
    val qualifiedReturnType by lazy {
        fqReturnType
    }


    val immediateReferenced by lazy {
        if (isPrimitive)
            null
        else EntityProcessor.getDeclaration(fqReturnType) as? KClassDeclaration
            ?: KClassDeclaration(resolvedType.declaration as KSClassDeclaration)
    }

    val immediateReferencedProperty by lazy {
        if (isForeign)
            immediateReferenced?.columns?.firstOrNull {
                referencedFiledName?.let { referencedPropertyName ->
                    it.propertyName == referencedPropertyName
                } ?: it.isPrimary
            }
        else null
    }

    val pkgName by lazy {
        declaration.packageName.asString()
    }

    val fqName by lazy {
        declaration.qualifiedName?.asString()
    }

    private val resolvedType by lazy {
        type.resolve()
    }


    val isNullable by lazy {
        resolvedType.isMarkedNullable
    }


    private val referencedClass by lazy {
        val declaration = resolvedType.declaration

        if (declaration is KSClassDeclaration) {
            KClassDeclaration(
                declaration
            )
        } else {
            Logger.error("Type referenced isn't of type class")
            null
        }
    }


    private val returnTypeSimpleName by lazy {
        fqReturnType.split('.').last()
    }

    var isForeign by mutableLazy {
        foreignAnnotation?.let {
            true
        } ?: !isPrimitive
    }

    val daoClass by lazy {
        if (isForeign) {
            resolvedType.declaration.qualifiedName?.asString()?.let { fqName ->
                EntityProcessor.getDeclaration(fqName) as? KClassDeclaration
                    ?: KClassDeclaration(resolvedType.declaration as KSClassDeclaration)
            }

        } else null
    }

    val daoFqName by lazy {
        daoClass?.daoFqName
    }

    val primitiveTypeSimpleString by lazy {
        if (isPrimitive)
            returnTypeSimpleName
        else {
            referencedClass?.let { _ ->

            }

        }

        "kotlin.Int"
    }

    /**
     * This implementation has a few flaws if the constructor
     * parameters are differently named
     * */
    val KClassDeclaration.inConstructor by contexedLazy {
        var result = false

        for (constructor in getConstructors()) {
            val parameter = constructor.parameters[this@KPropertyDeclaration]

            if (parameter != null) {
                result = true
                break
            }
        }

        result
    }

    val entityKeyTypeString by lazy {
        "EntityID<$primitiveTypeSimpleString>"
    }

    private fun getAnnotation(annotationQualifiedName: String): KSAnnotation? {
        var results: KSAnnotation? = null
        annotations.forEach {
            val qualifiedName = it.annotationType.resolve().declaration.qualifiedName?.asString()


            if (annotationQualifiedName == qualifiedName)
                results = it

        }

        return results
    }

    private val tableOverride by lazy {
        when (propertyName) {
            "id" -> true
            else -> false
        }
    }

    private val rightHandExpressionMethod by lazy {
        val methodName = if (isPrimary)
            checkNotNull(primitiveInitializers[primitiveTypeSimpleString]) {
                Logger.error("Can't resolve initializer expression for column $")
            }
        else if (isForeign)
            "reference"
        else checkNotNull(primitiveInitializers[qualifiedReturnType]) {
            Logger.error("Can't resolve initializer expression for type $qualifiedReturnType")
        }

        if (methodName == "varchar" && size > 255)
            "text"
        else
            methodName
    }

    private val tablePropertyAssignmentExpression by lazy {
        var rightHandExpression = "$rightHandExpressionMethod("

        rightHandExpression += """"$columnName""""

        when (rightHandExpressionMethod) {
            "text", "varchar" -> {
                if (rightHandExpressionMethod == "varchar")
                    rightHandExpression += ", length = ${if (size == 0) 50 else size}"
                if (!collate.isBlank)
                    rightHandExpression += """, collate = "$collate""""

                if (eagerLoading)
                    rightHandExpression += ", eagerLoading = $eagerLoading"
            }

            "reference" -> {
                referencedClass?.let { referencedClass ->
                    rightHandExpression += ", ${referencedClass.tableQualifiedName}"

                    onUpdate?.let { onUpdate ->
                        rightHandExpression += ", onUpdate = org.jetbrains.exposed.sql.ReferenceOption.$onUpdate"
                    }

                    onDelete?.let { onDelete ->
                        rightHandExpression += ", onDelete = org.jetbrains.exposed.sql.ReferenceOption.$onDelete"
                    }

                }

            }
        }

        rightHandExpression += ")"

        if (isPrimary && isAutoIncrement)
            rightHandExpression += ".autoIncrement()"

        if (isUnique)
            rightHandExpression += ".uniqueIndex()"

        if (isPrimary && propertyName == "id")
            rightHandExpression += ".entityId()"

        if (isNullable)
            if (isPrimary) {
                Logger.error("Nullable Property($fqName) Can't be marked as ${Annotations.PrimaryKey} ")
            } else rightHandExpression += ".nullable()"


        rightHandExpression
    }

    private val columnExplicitTypeParameter by lazy {
        if (isForeign) {
            primitiveTypeSimpleString
        } else {
            qualifiedReturnType
        }
    }

    private val columnExplicitType by lazy {
        var result = "Column<"

        val closer = when {
            isPrimary && propertyName != "id" -> ""
            isPrimary || isForeign -> {
                result += "org.jetbrains.exposed.dao.id.EntityID<"
                ">"
            }
            else -> ""
        }

        result += columnExplicitTypeParameter

        result += closer

        if (isNullable)
            if (isPrimary) {
                Logger.error("Nullable Property($fqName) Can't be marked as $PrimaryKey ")
            } else result += "?"

        result += ">"

        result
    }

    val tableFqName by lazy {
        entityClass.tableFqName
    }

    val primitivePath: String by lazy {
        var result = propertyName
        if (isForeign) {
            if (isNullable)
                result += "?"
            """$result.${immediateReferencedProperty?.primitivePath}"""
        } else result

    }

    val tableEqQuery by lazy {
        val result = """$tableFqName.$propertyName eq $primitivePath"""

        """($result)"""
    }

    fun tablePropertyString(indent: String = ""): String {

        var code = """|
            |$indent${if (tableOverride) "override " else ""}var $propertyName: $columnExplicitType = $tablePropertyAssignmentExpression""".trimMargin()

        if (isPrimary)
            code += if (propertyName == "id") {
                """|
                        |${indent}override val primaryKey: org.jetbrains.exposed.sql.Table.PrimaryKey = PrimaryKey(${propertyName})
                        |
                    """.trimMargin()
            } else {
                """|
                        |${indent}override val id: Column<org.jetbrains.exposed.dao.id.EntityID<$columnExplicitTypeParameter>> = $propertyName.entityId()
                        |${indent}override val primaryKey: org.jetbrains.exposed.sql.Table.PrimaryKey = PrimaryKey(id)
                        |
                    """.trimMargin()
            }

        return code
    }


}