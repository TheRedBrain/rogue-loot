package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record GeneratedRogueLootTableIdentifier(
		Identifier rogueLootTableIdentifier,
		Optional<BlockPos> optionalBlockPos,
		int rolls,
		int minChoices,
		int maxChoices
) {
	public static final Codec<GeneratedRogueLootTableIdentifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("rogue_loot_table_identifier").forGetter(x -> x.rogueLootTableIdentifier),
			BlockPos.CODEC.optionalFieldOf("optional_block_pos").forGetter(x -> x.optionalBlockPos),
			Codec.INT.fieldOf("rolls").forGetter(x -> x.rolls),
			Codec.INT.fieldOf("min_choices").forGetter(x -> x.minChoices),
			Codec.INT.fieldOf("max_choices").forGetter(x -> x.maxChoices)
	).apply(instance, GeneratedRogueLootTableIdentifier::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootTableIdentifier> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			GeneratedRogueLootTableIdentifier::rogueLootTableIdentifier,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
			GeneratedRogueLootTableIdentifier::optionalBlockPos,
			ByteBufCodecs.INT,
			GeneratedRogueLootTableIdentifier::rolls,
			ByteBufCodecs.INT,
			GeneratedRogueLootTableIdentifier::minChoices,
			ByteBufCodecs.INT,
			GeneratedRogueLootTableIdentifier::maxChoices,
			GeneratedRogueLootTableIdentifier::new
	);

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof GeneratedRogueLootTableIdentifier(
				Identifier rogueLootTableIdentifier1,
				Optional<BlockPos> optionalBlockPos1,
				int rolls1,
				int choices,
				int maxChoices1
		)) {
			return this.rogueLootTableIdentifier.equals(rogueLootTableIdentifier1)
					&& this.optionalBlockPos.equals(optionalBlockPos1)
					&& this.rolls == rolls1
					&& this.minChoices == choices
					&& this.maxChoices == maxChoices1;
		}
		return false;
	}

	public Identifier toIdentifier() {
		return Identifier.fromNamespaceAndPath(this.rogueLootTableIdentifier.getNamespace(), this.rogueLootTableIdentifier.getPath() + "_" + (this.optionalBlockPos.isPresent() ? this.optionalBlockPos.get().toShortString() + "_" : "") + "_" + this.rolls + "_" + this.minChoices + "_" + this.maxChoices);
	}

}
