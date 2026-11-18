package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface RogueLootTableEntryComponentType {
	Codec<RogueLootTableEntryComponentType> CODEC = RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY.byNameCodec().dispatch(RogueLootTableEntryComponentType::codec, c -> c);

	MapCodec<? extends RogueLootTableEntryComponentType> codec();

	void apply(ServerLevel serverLevel, ServerPlayer serverPlayer);
}
