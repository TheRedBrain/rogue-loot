package com.github.theredbrain.rogueloot.compatibility.stamina_attributes;

import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetStaminaType(
		float ratio
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetStaminaType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Stamina ratio can not be zero or smaller, but was " + value) : value > 1.0F ? DataResult.error(() -> "Stamina ratio can not be bigger than 1.0F, but was " + value) : DataResult.success(value)).fieldOf("stamina_ratio").forGetter(SetStaminaType::ratio)
					)
					.apply(i, SetStaminaType::new)
	);

	@Override
	public MapCodec<SetStaminaType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		if (this.ratio > 0.0F) {
			float newStamina = ((StaminaUsingEntity)serverPlayer).staminaattributes$getMaxStamina() * this.ratio;
			((StaminaUsingEntity)serverPlayer).staminaattributes$setStamina(newStamina);
		}
	}
}
