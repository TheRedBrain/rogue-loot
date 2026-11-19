package com.github.theredbrain.rogueloot.compatibility.mana_attributes;

import com.github.theredbrain.manaattributes.entity.ManaUsingEntity;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record AddManaType(
		float amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddManaType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.fieldOf("mana_amount").forGetter(AddManaType::amount)
					)
					.apply(i, AddManaType::new)
	);

	@Override
	public MapCodec<AddManaType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		((ManaUsingEntity)serverPlayer).manaattributes$addMana(this.amount);
	}
}
