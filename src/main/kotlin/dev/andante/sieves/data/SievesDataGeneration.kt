package dev.andante.sieves.data

import dev.andante.sieves.Sieves
import dev.andante.sieves.block.SievesBlocks
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.client.*
import net.minecraft.util.Identifier
import java.util.*

object SievesDataGeneration : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = gen.createPack()
        pack.addProvider(::SievesModelProvider)
        pack.addProvider(::SievesLootProvider)
    }
}

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

class SievesLootProvider(out: FabricDataOutput) : FabricBlockLootTableProvider(out) {
    override fun generate() {
        this.addDrop(SievesBlocks.SIEVE)
    }

}
