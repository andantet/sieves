package dev.andante.sieves

import dev.andante.sieves.block.SievesBlocks
import dev.andante.sieves.block.entity.SievesBlockEntityTypes
import dev.andante.sieves.item.SievesItems
import dev.andante.sieves.loot.context.SievesLootContextTypes
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items

object Sieves : ModInitializer {
    const val MOD_ID = "sieves"
    const val MOD_NAME = "Sieves"

    @Suppress("UnstableApiUsage")
    override fun onInitialize() {
        println("Initializing $MOD_NAME")

        SievesBlocks; SievesItems; SievesBlockEntityTypes; SievesLootContextTypes

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ItemGroupEvents.ModifyEntries {
            it.addAfter(Items.COMPOSTER, SievesBlocks.SIEVE)
        })
    }
}
