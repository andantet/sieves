package dev.andante.sieves.block.entity

import dev.andante.sieves.block.SieveBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SieveBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(SievesBlockEntityTypes.SIEVE, pos, state) {
    var sievingState: BlockState = Blocks.AIR.defaultState
    var sieveTime: Int = 0

    companion object {
        const val SIEVING_STATE_KEY: String = "SievingState"
        const val MAX_SIEVE_TIME: Int = 40

        fun serverTick(world: World, pos: BlockPos, state: BlockState, blockEntity: SieveBlockEntity) {
            if (blockEntity.sieveTime > 0) {
                blockEntity.sieveTime--
                blockEntity.updateListeners()
            } else if (!blockEntity.sievingState.isAir) {
                blockEntity.finishSieving()
            }
        }
    }

    fun sieve(state: BlockState): Boolean {
        if (!sievingState.isAir) {
            return false
        }

        sievingState = state
        sieveTime = MAX_SIEVE_TIME
        updateListeners()
        return true
    }

    fun finishSieving() {
        if (!sievingState.isAir) {
            if (world is ServerWorld) {
                val world: ServerWorld = world as ServerWorld
                val vec: Vec3d = Vec3d.ofBottomCenter(pos)

                world.spawnParticles(BlockStateParticleEffect(ParticleTypes.BLOCK, sievingState), vec.x, vec.y, vec.z, 10, 0.25, 0.25, 0.25, 0.0)
                world.playSound(null, vec.x, vec.y, vec.z, RegistryEntry.of(sievingState.soundGroup.breakSound), SoundCategory.BLOCKS, 1.0f, 1.0f, 0L)

                val lootManager: LootManager = world.server.lootManager
                val table: LootTable = lootManager.getTable(LootTables.ABANDONED_MINESHAFT_CHEST)
                table.generateLoot(
                    LootContext.Builder(world)
                        .random(world.random)
                        .parameter(LootContextParameters.ORIGIN, vec)
                        .build(LootContextTypes.CHEST)
                ).forEach {
                    val entity: Entity = ItemEntity(world, vec.x, vec.y, vec.z, it)
                    world.spawnEntity(entity)
                }

                sievingState = Blocks.AIR.defaultState
                SieveBlock.removeExtension(world, pos)
                updateListeners()
            }
        }
    }

    fun updateListeners() {
        markDirty()
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put(SIEVING_STATE_KEY, NbtHelper.fromBlockState(sievingState ?: Blocks.AIR.defaultState))
        nbt.putInt("SieveTime", sieveTime)
    }

    override fun readNbt(nbt: NbtCompound) {
        world?.run {
            if (nbt.contains(SIEVING_STATE_KEY, NbtElement.COMPOUND_TYPE.toInt())) {
                sievingState = NbtHelper.toBlockState(this.createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound(SIEVING_STATE_KEY))
            }
        }

        sieveTime = nbt.getInt("SieveTime")
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }
}
