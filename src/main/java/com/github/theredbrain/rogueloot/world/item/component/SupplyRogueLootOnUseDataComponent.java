package com.github.theredbrain.rogueloot.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record SupplyRogueLootOnUseDataComponent(
		Optional<Identifier> rogueLootTableIdentifier,
		String uniqueSavingToken,
		int rolls,
		int minChoices,
		int maxChoices,
		boolean consume
) {
	public static final SupplyRogueLootOnUseDataComponent DEFAULT = new SupplyRogueLootOnUseDataComponent(Optional.empty(), "", 3, 1, 1, true);
	public static final Codec<SupplyRogueLootOnUseDataComponent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Identifier.CODEC.optionalFieldOf("rogue_loot_table_identifier").forGetter(x -> x.rogueLootTableIdentifier),
							Codec.STRING.optionalFieldOf("unique_saving_token", "").forGetter(x -> x.uniqueSavingToken),
							Codec.INT.optionalFieldOf("rolls", 3).forGetter(x -> x.rolls),
							Codec.INT.optionalFieldOf("min_choices", 1).forGetter(x -> x.minChoices),
							Codec.INT.optionalFieldOf("max_choices", 1).forGetter(x -> x.maxChoices),
							Codec.BOOL.optionalFieldOf("consume", true).forGetter(x -> x.consume)
					)
					.apply(instance, SupplyRogueLootOnUseDataComponent::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, SupplyRogueLootOnUseDataComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC),
			SupplyRogueLootOnUseDataComponent::rogueLootTableIdentifier,
			ByteBufCodecs.STRING_UTF8,
			SupplyRogueLootOnUseDataComponent::uniqueSavingToken,
			ByteBufCodecs.INT,
			SupplyRogueLootOnUseDataComponent::rolls,
			ByteBufCodecs.INT,
			SupplyRogueLootOnUseDataComponent::minChoices,
			ByteBufCodecs.INT,
			SupplyRogueLootOnUseDataComponent::maxChoices,
			ByteBufCodecs.BOOL,
			SupplyRogueLootOnUseDataComponent::consume,
			SupplyRogueLootOnUseDataComponent::new
	);
}
