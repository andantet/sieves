package dev.andante.sieves.block

import dev.andante.sieves.Sieves
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object SievesBlocks {
    val SIEVE: Block = register("sieve", SieveBlock(
        FabricBlockSettings.of(Material.WOOD, MapColor.OAK_TAN)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
    ))

    val SIEVING_SIEVE = register("sieving_sieve", SievingSieveBlock(FabricBlockSettings.of(Material.WOOD, MapColor.OAK_TAN)))

    private fun register(id: String, block: Block): Block {
        return Registry.register(Registries.BLOCK, Identifier(Sieves.MOD_ID, id), block)
    }
}
