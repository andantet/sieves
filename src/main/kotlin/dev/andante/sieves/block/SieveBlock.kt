@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package dev.andante.sieves.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class SieveBlock(settings: Settings) : Block(settings) {
    companion object {
        val SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 0.0, 0.0, 2.0, 12.0, 2.0),
            createCuboidShape(14.0, 0.0, 0.0, 16.0, 12.0, 2.0),
            createCuboidShape(0.0, 0.0, 14.0, 2.0, 12.0, 16.0),
            createCuboidShape(14.0, 0.0, 14.0, 16.0, 12.0, 16.0),
            createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0)
        )
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    override fun canPlaceAt(
        state: BlockState,
        world: WorldView,
        pos: BlockPos
    ): Boolean {
        val floor: BlockPos = pos.down()
        return world.getBlockState(floor).isSideSolidFullSquare(world, floor, Direction.UP)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (canPlaceAt(state, world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }
}
