package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record AddExperienceLevelType(
		int amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddExperienceLevelType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.INT.optionalFieldOf("amount", 1).forGetter(AddExperienceLevelType::amount)
					)
					.apply(i, AddExperienceLevelType::new)
	);

	@Override
	public MapCodec<AddExperienceLevelType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.giveExperienceLevels(this.amount);

	}
}
