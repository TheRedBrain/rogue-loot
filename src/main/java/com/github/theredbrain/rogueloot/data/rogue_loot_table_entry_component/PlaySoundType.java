package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Optional;

public record PlaySoundType(
		Holder<SoundEvent> soundEventHolder,
		NumberProvider volume,
		NumberProvider pitch,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<PlaySoundType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							SoundEvent.CODEC.fieldOf("sound_event").forGetter(x -> x.soundEventHolder),
							NumberProviders.CODEC.fieldOf("volume").forGetter(x -> x.volume),
							NumberProviders.CODEC.fieldOf("pitch").forGetter(x -> x.pitch),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, PlaySoundType::new)
	);

	@Override
	public MapCodec<PlaySoundType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverLevel.playSound(
				null,
				serverPlayer.getX(),
				serverPlayer.getY(),
				serverPlayer.getZ(),
				this.soundEventHolder,
				SoundSource.PLAYERS,
				this.volume.getFloat(new LootContext.Builder(
								new LootParams.Builder(serverLevel)
										.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
										.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
										.create(LootContextParamSets.ADVANCEMENT_REWARD)
						).create(this.randomSequence)
				),
				this.pitch.getFloat(new LootContext.Builder(
								new LootParams.Builder(serverLevel)
										.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
										.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
										.create(LootContextParamSets.ADVANCEMENT_REWARD)
						).create(this.randomSequence)
				)
		);

	}
}
