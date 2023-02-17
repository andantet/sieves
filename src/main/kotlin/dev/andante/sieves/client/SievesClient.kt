package dev.andante.sieves.client

import dev.andante.sieves.block.SievesBlocks
import dev.andante.sieves.block.entity.SievesBlockEntityTypes
import dev.andante.sieves.client.render.block.entity.SieveBlockEntityRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

@Environment(EnvType.CLIENT)
object SievesClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(SievesBlocks.SIEVE, RenderLayer.getCutout())
        BlockEntityRendererFactories.register(SievesBlockEntityTypes.SIEVE, ::SieveBlockEntityRenderer)
    }
}
