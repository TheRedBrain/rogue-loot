package com.github.theredbrain.rogueloot.compatibility.stamina_attributes;

import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record AddStaminaType(
		float amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddStaminaType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.fieldOf("stamina_amount").forGetter(AddStaminaType::amount)
					)
					.apply(i, AddStaminaType::new)
	);

	@Override
	public MapCodec<AddStaminaType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		((StaminaUsingEntity)serverPlayer).staminaattributes$addStamina(this.amount);
	}
}
