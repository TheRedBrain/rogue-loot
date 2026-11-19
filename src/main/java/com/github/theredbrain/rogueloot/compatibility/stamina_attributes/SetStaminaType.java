package com.github.theredbrain.rogueloot.compatibility.stamina_attributes;

import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
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

public record SetStaminaType(
		NumberProvider ratio,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetStaminaType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
//							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Stamina ratio can not be zero or smaller, but was " + value) : value > 1.0F ? DataResult.error(() -> "Stamina ratio can not be bigger than 1.0F, but was " + value) : DataResult.success(value)).fieldOf("stamina_ratio").forGetter(SetStaminaType::ratio)
							NumberProviders.CODEC.fieldOf("ratio").forGetter(x -> x.ratio),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, SetStaminaType::new)
	);

	@Override
	public MapCodec<SetStaminaType> codec() {
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
			float newStamina = ((StaminaUsingEntity) serverPlayer).staminaattributes$getMaxStamina() * ratio;
			((StaminaUsingEntity) serverPlayer).staminaattributes$setStamina(newStamina);
		}
	}
}
