package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetExperienceLevelType(
		int amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetExperienceLevelType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.INT.optionalFieldOf("amount", 1).forGetter(SetExperienceLevelType::amount)
					)
					.apply(i, SetExperienceLevelType::new)
	);

	@Override
	public MapCodec<SetExperienceLevelType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.setExperienceLevels(this.amount);

	}
}
