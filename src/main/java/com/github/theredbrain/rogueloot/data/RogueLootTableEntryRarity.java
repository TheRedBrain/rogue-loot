package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record RogueLootTableEntryRarity(
		String assetPathAddition,
		int defaultWeight,
		int minWeight,
		int maxWeight,
		Optional<Identifier> weightInfluencingAttribute,
		double influenceMultiplier,
		LabelSettings banishedLabel,
		LabelSettings defaultLabel,
		LabelSettings reservedLabel,
		LabelSettings selectedLabel,
		Optional<Identifier> tooltipStyle
) {
	public static final RogueLootTableEntryRarity DEFAULT = new RogueLootTableEntryRarity("", 1, 1, 1, Optional.empty(), 1.0, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, Optional.empty());
	public static final Codec<RogueLootTableEntryRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("asset_path_addition", "").forGetter(x -> x.assetPathAddition),
			Codec.INT.optionalFieldOf("default_weight", 1).forGetter(x -> x.defaultWeight),
			Codec.INT.optionalFieldOf("min_weight", 1).forGetter(x -> x.minWeight),
			Codec.INT.optionalFieldOf("max_weight", 1).forGetter(x -> x.maxWeight),
			Identifier.CODEC.optionalFieldOf("weight_influencing_attribute").forGetter(x -> x.weightInfluencingAttribute),
			Codec.DOUBLE.optionalFieldOf("influence_multiplier", 1.0).forGetter(x -> x.influenceMultiplier),
			LabelSettings.CODEC.optionalFieldOf("banished_label", LabelSettings.DEFAULT).forGetter(x -> x.banishedLabel),
			LabelSettings.CODEC.optionalFieldOf("default_label", LabelSettings.DEFAULT).forGetter(x -> x.defaultLabel),
			LabelSettings.CODEC.optionalFieldOf("reserved_label", LabelSettings.DEFAULT).forGetter(x -> x.reservedLabel),
			LabelSettings.CODEC.optionalFieldOf("selected_label", LabelSettings.DEFAULT).forGetter(x -> x.selectedLabel),
			Identifier.CODEC.optionalFieldOf("tooltip_style").forGetter(x -> x.tooltipStyle)
	).apply(instance, RogueLootTableEntryRarity::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootTableEntryRarity> STREAM_CODEC = StreamCodec.ofMember(RogueLootTableEntryRarity::write, RogueLootTableEntryRarity::new);

	public RogueLootTableEntryRarity(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.DOUBLE.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.assetPathAddition);
		ByteBufCodecs.INT.encode(registryByteBuf, this.defaultWeight);
		ByteBufCodecs.INT.encode(registryByteBuf, this.minWeight);
		ByteBufCodecs.INT.encode(registryByteBuf, this.maxWeight);
		ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(registryByteBuf, this.weightInfluencingAttribute);
		ByteBufCodecs.DOUBLE.encode(registryByteBuf, this.influenceMultiplier);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.banishedLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.defaultLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.reservedLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.selectedLabel);
		ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(registryByteBuf, this.tooltipStyle);
	}

	@Nullable
	public static RogueLootTableEntryRarity getRogueLootTableEntryRarity(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTableEntryRarity rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityRegistry = registryAccess.lookup(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY);
		if (optionalRogueLootTableEntryRarityRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityReference = optionalRogueLootTableEntryRarityRegistry.get().get(identifier);
			if (optionalRogueLootTableEntryRarityReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootTableEntryRarityReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
