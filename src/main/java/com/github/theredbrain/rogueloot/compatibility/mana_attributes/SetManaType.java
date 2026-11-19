package com.github.theredbrain.rogueloot.compatibility.mana_attributes;

import com.github.theredbrain.manaattributes.entity.ManaUsingEntity;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetManaType(
		float ratio
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetManaType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Mana ratio can not be zero or smaller, but was " + value) : value > 1.0F ? DataResult.error(() -> "Mana ratio can not be bigger than 1.0F, but was " + value) : DataResult.success(value)).fieldOf("mana_ratio").forGetter(SetManaType::ratio)
					)
					.apply(i, SetManaType::new)
	);

	@Override
	public MapCodec<SetManaType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		if (this.ratio > 0.0F) {
			float newMana = ((ManaUsingEntity)serverPlayer).manaattributes$getMaxMana() * this.ratio;
			((ManaUsingEntity)serverPlayer).manaattributes$setMana(newMana);
		}
	}
}
