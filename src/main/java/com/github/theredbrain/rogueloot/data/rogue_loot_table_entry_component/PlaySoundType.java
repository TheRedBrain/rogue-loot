package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public record PlaySoundType(
		Holder<SoundEvent> soundEventHolder,
		float volume
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<PlaySoundType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							SoundEvent.CODEC.fieldOf("sound_event").forGetter(PlaySoundType::soundEventHolder),
							Codec.FLOAT.validate(value -> value <= 0.0F ? DataResult.error(() -> "Damage amount can not be zero or smaller, but was " + value) : DataResult.success(value)).optionalFieldOf("volume", 1.0F).forGetter(PlaySoundType::volume)
					)
					.apply(i, PlaySoundType::new)
	);

	@Override
	public MapCodec<PlaySoundType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), this.soundEventHolder, SoundSource.PLAYERS, this.volume, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);

	}
}
