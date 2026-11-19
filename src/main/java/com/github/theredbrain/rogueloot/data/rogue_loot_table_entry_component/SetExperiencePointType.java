package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetExperiencePointType(
		int amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetExperiencePointType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.INT.optionalFieldOf("amount", 1).forGetter(SetExperiencePointType::amount)
					)
					.apply(i, SetExperiencePointType::new)
	);

	@Override
	public MapCodec<SetExperiencePointType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.setExperiencePoints(this.amount);
	}
}
