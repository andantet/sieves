package dev.andante.sieves.item

import dev.andante.sieves.Sieves
import dev.andante.sieves.block.SievesBlocks
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object SievesItems {
    val SIEVE: Item = register("sieve", BlockItem(SievesBlocks.SIEVE, FabricItemSettings()))

    private fun register(id: String, item: Item): Item {
        return Registry.register(Registries.ITEM, Identifier(Sieves.MOD_ID, id), item)
    }
}
