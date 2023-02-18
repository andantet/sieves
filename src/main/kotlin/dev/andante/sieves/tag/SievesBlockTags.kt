package dev.andante.sieves.tag

import dev.andante.sieves.Sieves
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object SievesBlockTags {
    val SIEVABLE_BLOCKS = register("sievable_blocks")
    val SIEVE_PASS_THROUGH_FLOOR = register("sieve_pass_through_floor")

    private fun register(id: String): TagKey<Block> {
        return TagKey.of(RegistryKeys.BLOCK, Identifier(Sieves.MOD_ID, id))
    }
}
