package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Optional;

public record HealToXPercentType(
		NumberProvider ratio,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<HealToXPercentType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
//							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Health ratio can not be zero or smaller, but was " + value) : value > 1.0F ? DataResult.error(() -> "Health ratio can not be bigger than 1.0F, but was " + value) : DataResult.success(value)).fieldOf("health_ratio").forGetter(HealToXPercentType::ratio)
							NumberProviders.CODEC.fieldOf("ratio").forGetter(x -> x.ratio),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, HealToXPercentType::new)
	);

	@Override
	public MapCodec<HealToXPercentType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		float ratio = this.ratio.getFloat(new LootContext.Builder(
						new LootParams.Builder(serverLevel)
								.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
								.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
								.create(LootContextParamSets.ADVANCEMENT_REWARD)
				).create(this.randomSequence)
		);
		ratio = Math.clamp(ratio, 0.0F, 1.0F); // TODO this needs to be documented
		if (ratio > 0.0F) {
			float newHealth = serverPlayer.getMaxHealth() * ratio;
			serverPlayer.setHealth(newHealth);
		}
	}
}
