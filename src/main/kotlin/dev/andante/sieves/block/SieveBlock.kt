@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package dev.andante.sieves.block

import dev.andante.sieves.block.entity.SieveBlockEntity
import dev.andante.sieves.block.entity.SievesBlockEntityTypes
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class SieveBlock(settings: Settings) : BlockWithEntity(settings) {
    companion object {
        val SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            createCuboidShape(0.0, 5.0, 0.0, 16.0, 7.0, 16.0),
            createCuboidShape(0.0, 0.0, 0.0, 2.0, 12.0, 2.0),
            createCuboidShape(14.0, 0.0, 0.0, 16.0, 12.0, 2.0),
            createCuboidShape(0.0, 0.0, 14.0, 2.0, 12.0, 16.0),
            createCuboidShape(14.0, 0.0, 14.0, 16.0, 12.0, 16.0)
        )

        fun isSievable(state: BlockState): Boolean {
            return state.isIn(BlockTags.SAND)
        }

        fun removeExtension(world: World, pos: BlockPos) {
            val extensionPos: BlockPos = pos.up()
            if (world.getBlockState(extensionPos).block is SievingSieveBlock) {
                world.setBlockState(extensionPos, Blocks.AIR.defaultState)
            }
        }
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
        val floorPos: BlockPos = pos.down()
        val floorState: BlockState = world.getBlockState(floorPos)
        return floorState.block is HopperBlock || floorState.isSideSolidFullSquare(world, floorPos, Direction.UP)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (isSievable(world.getBlockState(pos.up()))) {
            this.scheduleTick(world, pos, 8)
        }

        return if (canPlaceAt(state, world, pos)) super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos) else Blocks.AIR.defaultState
    }

    fun scheduleTick(world: WorldAccess, pos: BlockPos, delay: Int) {
        if (!world.isClient && !world.blockTickScheduler.isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, delay)
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        super.onBreak(world, pos, state, player)
        removeExtension(world, pos)
    }

    override fun scheduledTick(
        state: BlockState,
        world: ServerWorld,
        pos: BlockPos,
        random: Random
    ) {
        val sieveStatePos: BlockPos = pos.up()
        val sieveState: BlockState = world.getBlockState(sieveStatePos)
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if (blockEntity is SieveBlockEntity && blockEntity.sieve(sieveState)) {
            world.setBlockState(sieveStatePos, SievesBlocks.SIEVING_SIEVE.defaultState)
        }
    }

    override fun onEntityLand(world: BlockView, entity: Entity) {
        super.onEntityLand(world, entity)

        if (world is ServerWorld) {
            if (entity is FallingBlockEntity && isSievable(entity.blockState)) {
                scheduleTick(world, entity.blockPos.down(), 0)
            }
        }
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (world.isClient) null else checkType(type, SievesBlockEntityTypes.SIEVE, SieveBlockEntity::serverTick)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SieveBlockEntity(pos, state)
    }
}
