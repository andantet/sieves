package dev.andante.sieves.client

import dev.andante.sieves.block.SievesBlocks
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

@Environment(EnvType.CLIENT)
object SievesClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(SievesBlocks.SIEVE, RenderLayer.getCutout())
    }
}
