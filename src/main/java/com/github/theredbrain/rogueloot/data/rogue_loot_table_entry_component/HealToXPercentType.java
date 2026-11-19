package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

public record HealToXPercentType(
		float healthRatio
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<HealToXPercentType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Health Ration can not be zero or smaller, but was " + value) : value > 1.0F ? DataResult.error(() -> "Health ratio can not be bigger than 1.0F, but was " + value) : DataResult.success(value)).fieldOf("health_ratio").forGetter(HealToXPercentType::healthRatio)
					)
					.apply(i, HealToXPercentType::new)
	);

	@Override
	public MapCodec<HealToXPercentType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		if (this.healthRatio > 0.0F) {
			float newHealth = serverPlayer.getMaxHealth() * this.healthRatio;
			serverPlayer.setHealth(newHealth);
		}
	}
}
