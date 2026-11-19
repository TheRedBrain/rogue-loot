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

public record HealXAmountType(
		NumberProvider amount,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<HealXAmountType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							NumberProviders.CODEC.fieldOf("amount").forGetter(x -> x.amount),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, HealXAmountType::new)
	);

	@Override
	public MapCodec<HealXAmountType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.heal(
				this.amount.getFloat(new LootContext.Builder(
								new LootParams.Builder(serverLevel)
										.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
										.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
										.create(LootContextParamSets.ADVANCEMENT_REWARD)
						).create(this.randomSequence)
				)
		);
	}
}
