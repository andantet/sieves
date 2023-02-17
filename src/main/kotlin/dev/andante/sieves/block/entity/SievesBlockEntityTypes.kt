package dev.andante.sieves.block.entity

import dev.andante.sieves.Sieves
import dev.andante.sieves.block.SievesBlocks
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object SievesBlockEntityTypes {
    val SIEVE: BlockEntityType<SieveBlockEntity> = register("sieve", FabricBlockEntityTypeBuilder.create(::SieveBlockEntity, SievesBlocks.SIEVE))

    private fun <T : BlockEntity> register(id: String, builder: FabricBlockEntityTypeBuilder<T>): BlockEntityType<T> {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier(Sieves.MOD_ID, id), builder.build())
    }
}
