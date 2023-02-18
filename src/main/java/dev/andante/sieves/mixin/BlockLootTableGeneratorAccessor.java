package dev.andante.sieves.mixin;

import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockLootTableGenerator.class)
public interface BlockLootTableGeneratorAccessor {
    @Accessor Map<Identifier, LootTable.Builder> getLootTables();
}
