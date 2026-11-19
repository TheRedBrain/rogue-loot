package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Optional;

public record DealDamageType(
		Holder<DamageType> damageTypeHolder,
		NumberProvider amount,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<DealDamageType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							DamageType.CODEC.fieldOf("damage_type").forGetter(x -> x.damageTypeHolder),
//							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Damage amount can not be zero or smaller, but was " + value) : DataResult.success(value)).fieldOf("amount").forGetter(DealDamageType::amount)
							NumberProviders.CODEC.fieldOf("amount").forGetter(x -> x.amount),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, DealDamageType::new)
	);

	@Override
	public MapCodec<DealDamageType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		// TODO this needs to be documented
		float amount = Math.min(0.0F, this.amount.getInt(new LootContext.Builder(
						new LootParams.Builder(serverLevel)
								.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
								.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
								.create(LootContextParamSets.ADVANCEMENT_REWARD)
				).create(this.randomSequence)
		));
		if (amount > 0.0F) {
			serverPlayer.hurtServer(
					serverLevel,
					new DamageSource(this.damageTypeHolder),
					amount
			);
		}
	}
}
