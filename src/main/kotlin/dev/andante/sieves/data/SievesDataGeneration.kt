@file:Suppress("CAST_NEVER_SUCCEEDS")

package dev.andante.sieves.data

import dev.andante.sieves.Sieves
import dev.andante.sieves.block.SievesBlocks
import dev.andante.sieves.block.entity.SieveBlockEntity
import dev.andante.sieves.loot.context.SievesLootContextTypes
import dev.andante.sieves.tag.SievesBlockTags
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.client.*
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.EmptyEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.EnchantRandomlyLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.Identifier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

object SievesDataGeneration : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = gen.createPack()
        pack.addProvider(::SievesModelProvider)
        pack.addProvider(::SievesBlockLootTableProvider)
        pack.addProvider(::SievesSieveLootTableProvider)
        pack.addProvider(::SievesBlockTagProvider)
    }
}

/* Models */

class SievesModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        gen.registerSieve()
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
    }

    companion object {
        fun BlockStateModelGenerator.registerSieve() {
            val block: Block = SievesBlocks.SIEVE
            val map: TextureMap = TextureMap().put(SievesTextureKeys.NET, TextureMap.getId(block)).put(TextureKey.TEXTURE, TextureMap.getId(Blocks.OAK_PLANKS))
            val id: Identifier = SievesModels.TEMPLATE_SIEVE.upload(block, map, this.modelCollector)
            this.registerParentedItemModel(block, id)
            this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant().put(VariantSettings.MODEL, id)))
        }
    }

    object SievesModels {
        val TEMPLATE_SIEVE: Model = block("template_sieve", SievesTextureKeys.NET, TextureKey.TEXTURE)

        private fun block(parent: String, vararg required: TextureKey): Model {
            return Model(Optional.of(Identifier(Sieves.MOD_ID, "block/$parent")), Optional.empty(), *required)
        }
    }

    object SievesTextureKeys {
        val NET: TextureKey = TextureKey.of("net")
    }
}

/* Tags */

class SievesBlockTagProvider(out: FabricDataOutput, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>) : FabricTagProvider.BlockTagProvider(out, registriesFuture) {
    override fun configure(arg: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(SievesBlockTags.SIEVABLE_BLOCKS).add(Blocks.SAND, Blocks.RED_SAND)
        getOrCreateTagBuilder(SievesBlockTags.SIEVE_PASS_THROUGH_FLOOR).forceAddTag(BlockTags.SLABS)
    }
}

/* Block Loot Tables */

class SievesBlockLootTableProvider(out: FabricDataOutput) : FabricBlockLootTableProvider(out) {
    override fun generate() {
        this.addDrop(SievesBlocks.SIEVE)
    }
}

/* Sieve Loot Tables */

class SievesSieveLootTableProvider(out: FabricDataOutput) : SimpleFabricLootTableProvider(out, SievesLootContextTypes.SIEVE) {
    override fun accept(exporter: BiConsumer<Identifier, LootTable.Builder>) {
        exporter.accept(SieveBlockEntity.createLootTableForBlock(Blocks.SAND), createLootTable())
        exporter.accept(SieveBlockEntity.createLootTableForBlock(Blocks.RED_SAND), createLootTable())
    }

    private fun createLootTable(): LootTable.Builder {
        return LootTable.builder().pool(
            LootPool.builder().rolls(UniformLootNumberProvider.create(1.0f, 2.0f)).with(
                ItemEntry.builder(Items.DIAMOND).weight(5).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(
                            1.0f,
                            3.0f
                        )
                    )
                )
            ).with(
                ItemEntry.builder(Items.IRON_INGOT).weight(15).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 5.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(2.0f, 7.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.EMERALD).weight(15).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 3.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.BONE).weight(25).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(4.0f, 6.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.SPIDER_EYE).weight(25).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 3.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.ROTTEN_FLESH).weight(25).apply(
                    SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0f, 7.0f))
                )
            ).with(
                ItemEntry.builder(Items.SADDLE).weight(20)
            ).with(
                ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15)
            ).with(
                ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10)
            ).with(
                ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5)
            ).with(
                ItemEntry.builder(Items.BOOK).weight(20).apply(EnchantRandomlyLootFunction.builder())
            ).with(
                ItemEntry.builder(Items.GOLDEN_APPLE).weight(20)
            ).with(
                ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2)
            ).with(
                EmptyEntry.builder().weight(20)
            )
        ).pool(
            LootPool.builder().rolls(ConstantLootNumberProvider.create(4.0f)).with(
                ItemEntry.builder(Items.BONE).weight(10).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 8.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 8.0f)
                    )
                )
            ).with(
                ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(
                    SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 8.0f))
                )
            ).with(
                ItemEntry.builder(Items.STRING).weight(10).apply(
                    SetCountLootFunction.builder(
                        UniformLootNumberProvider.create(1.0f, 8.0f)
                    )
                )
            )
        )
    }
}
