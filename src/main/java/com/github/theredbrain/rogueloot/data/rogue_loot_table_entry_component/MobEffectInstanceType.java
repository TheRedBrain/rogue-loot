package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public record MobEffectInstanceType(
		MobEffectInstance mobEffectInstance,
		Optional<StackingMode> optionalStackingMode
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<MobEffectInstanceType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							MobEffectInstance.CODEC.fieldOf("mob_effect_instance").forGetter(x -> x.mobEffectInstance),
							StackingMode.CODEC.optionalFieldOf("stacking_mode").forGetter(x -> x.optionalStackingMode)
					)
					.apply(instance, MobEffectInstanceType::new)
	);

	@Override
	public MapCodec<MobEffectInstanceType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		if (this.optionalStackingMode.isEmpty()) {
			serverPlayer.addEffect(new MobEffectInstance(this.mobEffectInstance));
		} else {
			int existingDuration = 0;
			int existingAmplifier = -1;
			MobEffectInstance existingEffectInstance = serverPlayer.getEffect(this.mobEffectInstance.getEffect());
			if (existingEffectInstance != null) {
				existingDuration = existingEffectInstance.getDuration();
				existingAmplifier = existingEffectInstance.getAmplifier();
				serverPlayer.removeEffect(this.mobEffectInstance.getEffect());
			}
			int newDuration = this.mobEffectInstance.getDuration();
			int newAmplifier = this.mobEffectInstance.getAmplifier();
			if (this.optionalStackingMode.get() == StackingMode.AMPLIFIER || this.optionalStackingMode.get() == StackingMode.BOTH) {
				newAmplifier = newAmplifier + existingAmplifier + 1;
			}
			if (this.optionalStackingMode.get() == StackingMode.DURATION || this.optionalStackingMode.get() == StackingMode.BOTH) {
				newDuration += existingDuration;
			}
			serverPlayer.addEffect(new MobEffectInstance(this.mobEffectInstance.getEffect(), newDuration, newAmplifier, this.mobEffectInstance.isAmbient(), this.mobEffectInstance.isVisible(), this.mobEffectInstance.showIcon()));
		}
	}

	public enum StackingMode implements StringRepresentable {
		AMPLIFIER("amplifier"),
		BOTH("both"),
		DURATION("duration"),
		NONE("none");

		public static final Codec<StackingMode> CODEC = StringRepresentable.fromEnum(StackingMode::values);

		private final String name;

		StackingMode(final String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

}
