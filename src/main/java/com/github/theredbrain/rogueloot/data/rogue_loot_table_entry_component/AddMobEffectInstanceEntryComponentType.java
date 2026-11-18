package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public record AddMobEffectInstanceEntryComponentType(
		MobEffectInstance mobEffectInstance
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddMobEffectInstanceEntryComponentType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							MobEffectInstance.CODEC.fieldOf("mob_effect_instance").forGetter(AddMobEffectInstanceEntryComponentType::mobEffectInstance)
					)
					.apply(i, AddMobEffectInstanceEntryComponentType::new)
	);

	@Override
	public MapCodec<AddMobEffectInstanceEntryComponentType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		serverPlayer.addEffect(new MobEffectInstance(this.mobEffectInstance));
	}
}
