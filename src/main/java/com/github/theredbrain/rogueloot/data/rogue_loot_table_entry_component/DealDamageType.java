package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public record DealDamageType(
		Holder<DamageType> damageTypeHolder,
		float amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<DealDamageType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							DamageType.CODEC.fieldOf("damage_type").forGetter(DealDamageType::damageTypeHolder),
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Damage amount can not be zero or smaller, but was " + value) : DataResult.success(value)).fieldOf("amount").forGetter(DealDamageType::amount)
					)
					.apply(i, DealDamageType::new)
	);

	@Override
	public MapCodec<DealDamageType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.hurtServer(serverLevel, new DamageSource(this.damageTypeHolder), this.amount);
	}
}
