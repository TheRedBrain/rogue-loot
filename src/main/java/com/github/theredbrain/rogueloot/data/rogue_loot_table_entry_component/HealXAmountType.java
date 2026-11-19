package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

public record HealXAmountType(
		float healAmount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<HealXAmountType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Health amount can not be zero or smaller, but was " + value) : DataResult.success(value)).fieldOf("heal_amount").forGetter(HealXAmountType::healAmount)
					)
					.apply(i, HealXAmountType::new)
	);

	@Override
	public MapCodec<HealXAmountType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.heal(this.healAmount);
	}
}
