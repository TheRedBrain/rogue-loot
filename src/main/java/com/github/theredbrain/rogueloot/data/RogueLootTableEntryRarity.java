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
		int weight,
		Optional<CommonDataStructures.AttributeInfluencedIntegerModifier> optionalWeightModifier,
		CommonDataStructures.LabelSettings banishedLabel,
		CommonDataStructures.LabelSettings defaultLabel,
		CommonDataStructures.LabelSettings reservedLabel,
		CommonDataStructures.LabelSettings selectedLabel,
		Optional<Identifier> tooltipStyle
) {
	public static final RogueLootTableEntryRarity DEFAULT = new RogueLootTableEntryRarity("", 1, Optional.empty(), CommonDataStructures.LabelSettings.DEFAULT, CommonDataStructures.LabelSettings.DEFAULT, CommonDataStructures.LabelSettings.DEFAULT, CommonDataStructures.LabelSettings.DEFAULT, Optional.empty());
	public static final Codec<RogueLootTableEntryRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("asset_path_addition", "").forGetter(x -> x.assetPathAddition),
			Codec.INT.optionalFieldOf("weight", 1).forGetter(x -> x.weight),
			CommonDataStructures.AttributeInfluencedIntegerModifier.CODEC.optionalFieldOf("weight_modifier").forGetter(x -> x.optionalWeightModifier),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("banished_label", CommonDataStructures.LabelSettings.DEFAULT).forGetter(x -> x.banishedLabel),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("default_label", CommonDataStructures.LabelSettings.DEFAULT).forGetter(x -> x.defaultLabel),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("reserved_label", CommonDataStructures.LabelSettings.DEFAULT).forGetter(x -> x.reservedLabel),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("selected_label", CommonDataStructures.LabelSettings.DEFAULT).forGetter(x -> x.selectedLabel),
			Identifier.CODEC.optionalFieldOf("tooltip_style").forGetter(x -> x.tooltipStyle)
	).apply(instance, RogueLootTableEntryRarity::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootTableEntryRarity> STREAM_CODEC = StreamCodec.ofMember(RogueLootTableEntryRarity::write, RogueLootTableEntryRarity::new);

	public RogueLootTableEntryRarity(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.AttributeInfluencedIntegerModifier.STREAM_CODEC).decode(registryByteBuf),
				CommonDataStructures.LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				CommonDataStructures.LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				CommonDataStructures.LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				CommonDataStructures.LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.assetPathAddition);
		ByteBufCodecs.INT.encode(registryByteBuf, this.weight);
		ByteBufCodecs.optional(CommonDataStructures.AttributeInfluencedIntegerModifier.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.optionalWeightModifier);
		CommonDataStructures.LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.banishedLabel);
		CommonDataStructures.LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.defaultLabel);
		CommonDataStructures.LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.reservedLabel);
		CommonDataStructures.LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.selectedLabel);
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
