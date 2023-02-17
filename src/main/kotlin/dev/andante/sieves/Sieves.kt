package dev.andante.sieves

import dev.andante.sieves.block.SievesBlocks
import dev.andante.sieves.block.entity.SievesBlockEntityTypes
import dev.andante.sieves.item.SievesItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items

object Sieves : ModInitializer {
    const val MOD_ID = "sieves"

    @Suppress("UnstableApiUsage")
    override fun onInitialize() {
        println("Initializing $MOD_ID")

        SievesBlocks; SievesItems; SievesBlockEntityTypes

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ItemGroupEvents.ModifyEntries {
            it.addAfter(Items.COMPOSTER, SievesBlocks.SIEVE)
        })
    }
}
