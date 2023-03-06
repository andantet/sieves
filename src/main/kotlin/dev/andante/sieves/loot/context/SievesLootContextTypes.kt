package dev.andante.sieves.loot.context

import dev.andante.sieves.Sieves
import dev.andante.sieves.mixin.access.LootContextTypesAccessor
import net.minecraft.loot.context.LootContextType
import net.minecraft.util.Identifier
import java.util.function.Consumer

object SievesLootContextTypes {
    val SIEVE: LootContextType = register("sieve") { }

    private fun register(id: String, typeBuilder: Consumer<LootContextType.Builder>): LootContextType {
        val builder = LootContextType.Builder()
        typeBuilder.accept(builder)
        val type = builder.build()
        val identifier = Identifier(Sieves.MOD_ID, id)
        val existing: LootContextType? = LootContextTypesAccessor.getMAP().put(identifier, type)
        check(existing == null) { "Loot table parameter set $identifier is already registered" }
        return type
    }
}
