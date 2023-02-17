@file:Suppress("OVERRIDE_DEPRECATION")

package dev.andante.sieves.block

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class SievingSieveBlock(settings: Settings) : Block(settings) {
    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.fullCube()
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.INVISIBLE
    }

    override fun getPistonBehavior(state: BlockState): PistonBehavior {
        return PistonBehavior.BLOCK
    }
}
