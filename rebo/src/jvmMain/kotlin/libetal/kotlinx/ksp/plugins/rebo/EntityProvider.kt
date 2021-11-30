package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class EntityProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) = EntityProcessor(
        environment
    )
}