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
		LabelSettings banishedLabel,
		LabelSettings defaultLabel,
		LabelSettings reservedLabel,
		LabelSettings selectedLabel
) {
	public static final RogueLootTableEntryRarity DEFAULT = new RogueLootTableEntryRarity("", 1, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT);
	public static final Codec<RogueLootTableEntryRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("asset_path_addition", "").forGetter(x -> x.assetPathAddition),
			Codec.INT.optionalFieldOf("default_weight", 1).forGetter(x -> x.defaultWeight),
			LabelSettings.CODEC.optionalFieldOf("banished_label", LabelSettings.DEFAULT).forGetter(x -> x.banishedLabel),
			LabelSettings.CODEC.optionalFieldOf("default_label", LabelSettings.DEFAULT).forGetter(x -> x.defaultLabel),
			LabelSettings.CODEC.optionalFieldOf("reserved_label", LabelSettings.DEFAULT).forGetter(x -> x.reservedLabel),
			LabelSettings.CODEC.optionalFieldOf("selected_label", LabelSettings.DEFAULT).forGetter(x -> x.selectedLabel)
	).apply(instance, RogueLootTableEntryRarity::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootTableEntryRarity> STREAM_CODEC = StreamCodec.ofMember(RogueLootTableEntryRarity::write, RogueLootTableEntryRarity::new);

	public RogueLootTableEntryRarity(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.assetPathAddition);
		ByteBufCodecs.INT.encode(registryByteBuf, this.defaultWeight);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.banishedLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.defaultLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.reservedLabel);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.selectedLabel);
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
