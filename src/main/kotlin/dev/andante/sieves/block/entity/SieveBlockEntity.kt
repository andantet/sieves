package dev.andante.sieves.block.entity

import dev.andante.sieves.Sieves
import dev.andante.sieves.block.SieveBlock
import dev.andante.sieves.loot.context.SievesLootContextTypes
import dev.andante.sieves.tag.SievesBlockTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.BlockStateParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SieveBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(SievesBlockEntityTypes.SIEVE, pos, state) {
    var sievingState: BlockState = Blocks.AIR.defaultState
    var sieveTime: Int = 0

    companion object {
        const val SIEVING_STATE_KEY: String = "SievingState"
        const val MAX_SIEVE_TIME: Int = 40

        private val LOOT_TABLE_CACHE: MutableMap<Block, Identifier> = mutableMapOf()

        fun serverTick(world: World, pos: BlockPos, state: BlockState, blockEntity: SieveBlockEntity) {
            if (blockEntity.sieveTime > 0) {
                blockEntity.sieveTime--
                blockEntity.updateListeners()
            } else if (!blockEntity.sievingState.isAir) {
                blockEntity.finishSieving()
            }
        }

        fun createLootTableForBlock(block: Block): Identifier {
            val blockId: Identifier = Registries.BLOCK.getId(block)
            return Identifier(Sieves.MOD_ID, "gameplay/sieve/${blockId.namespace}/${blockId.path}")
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
        if (sievingState.isAir || world !is ServerWorld) {
            return
        }

        val world: ServerWorld = world as ServerWorld
        val floorPos: BlockPos = pos.down()
        val floorState: BlockState = world.getBlockState(floorPos)

        val vec: Vec3d = Vec3d.ofCenter(pos)
        world.spawnParticles(BlockStateParticleEffect(ParticleTypes.BLOCK, sievingState), vec.x, vec.y, vec.z, 10, 0.25, 0.25, 0.25, 0.0)
        world.playSound(null, vec.x, vec.y, vec.z, RegistryEntry.of(sievingState.soundGroup.breakSound), SoundCategory.BLOCKS, 1.0f, 1.0f, 0L)

        val block: Block = sievingState.block
        if (LOOT_TABLE_CACHE[block] == null) {
            LOOT_TABLE_CACHE[block] = createLootTableForBlock(block)
        }

        val lootManager: LootManager = world.server.lootManager
        val table: LootTable = lootManager.getTable(LOOT_TABLE_CACHE[block])
        val itemVec: Vec3d = if (floorState.isIn(SievesBlockTags.SIEVE_PASS_THROUGH_FLOOR)) Vec3d.ofBottomCenter(floorPos) else Vec3d.ofCenter(pos)
        table.generateLoot(LootContext.Builder(world).random(world.random).build(SievesLootContextTypes.SIEVE)).forEach {
            val entity: Entity = ItemEntity(world, itemVec.x, itemVec.y, itemVec.z, it)
            world.spawnEntity(entity)
        }

        sievingState = Blocks.AIR.defaultState
        SieveBlock.removeExtension(world, pos)
        updateListeners()
    }

    fun updateListeners() {
        markDirty()
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL)
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put(SIEVING_STATE_KEY, NbtHelper.fromBlockState(sievingState))
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
