package dev.andante.sieves.client.render.block.entity

import dev.andante.sieves.block.entity.SieveBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class SieveBlockEntityRenderer(context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<SieveBlockEntity> {
    private val renderManager: BlockRenderManager = context.renderManager

    override fun render(
        entity: SieveBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        entity.world?.run {
            (entity.sievingState to this).run {
                matrices.push()

                val scale = 1 - (2 / 16f)
                matrices.translate(0.5, 0.5, 0.5)
                matrices.scale(scale, scale, scale)
                matrices.translate(-0.5, -0.5, -0.5)

                val time: Float = entity.sieveTime.toFloat()
                val max: Int = SieveBlockEntity.MAX_SIEVE_TIME
                val progress = if (time > max) 1f else (1f - time / max).coerceAtMost(1f).coerceAtLeast(0f)
                matrices.translate(0.0, 1.0 - (0.4f * progress), 0.0)

                renderManager.renderBlock(this.first, entity.pos, this.second, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, this.second.random)
                matrices.pop()
            }
        }
    }
}
