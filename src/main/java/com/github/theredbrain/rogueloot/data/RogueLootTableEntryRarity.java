package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootDynamicRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record RogueLootTableEntryRarity(
		String assetPathAddition,
		int defaultWeight,
		List<ChatFormatting> defaultLabelFormatting,
		boolean defaultLabelHasShadow,
		List<ChatFormatting> selectedLabelFormatting,
		boolean selectedLabelHasShadow,
		List<ChatFormatting> banishedLabelFormatting,
		boolean banishedLabelHasShadow
) {
	public static final RogueLootTableEntryRarity DEFAULT = new RogueLootTableEntryRarity("", 1, List.of(), true, List.of(), true, List.of(), true);
	public static final Codec<RogueLootTableEntryRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("asset_path_addition", "").forGetter(x -> x.assetPathAddition),
			Codec.INT.optionalFieldOf("default_weight", 1).forGetter(x -> x.defaultWeight),
			ChatFormatting.CODEC.listOf().optionalFieldOf("default_label_formatting", List.of()).forGetter(x -> x.defaultLabelFormatting),
			Codec.BOOL.optionalFieldOf("default_label_has_shadow", true).forGetter(x -> x.defaultLabelHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("selected_label_formatting", List.of()).forGetter(x -> x.selectedLabelFormatting),
			Codec.BOOL.optionalFieldOf("selected_label_has_shadow", true).forGetter(x -> x.selectedLabelHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("banished_label_formatting", List.of()).forGetter(x -> x.banishedLabelFormatting),
			Codec.BOOL.optionalFieldOf("banished_label_has_shadow", true).forGetter(x -> x.defaultLabelHasShadow)
	).apply(instance, RogueLootTableEntryRarity::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootTableEntryRarity> STREAM_CODEC = StreamCodec.ofMember(RogueLootTableEntryRarity::write, RogueLootTableEntryRarity::new);

	public RogueLootTableEntryRarity(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.INT.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.assetPathAddition);
		ByteBufCodecs.INT.encode(registryByteBuf, this.defaultWeight);
		registryByteBuf.writeCollection(this.defaultLabelFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.defaultLabelHasShadow);
		registryByteBuf.writeCollection(this.selectedLabelFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.selectedLabelHasShadow);
		registryByteBuf.writeCollection(this.banishedLabelFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.banishedLabelHasShadow);
	}

	@Nullable
	public static RogueLootTableEntryRarity getRogueLootTableEntryRarity(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTableEntryRarity rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY);
		if (optionalRogueLootTableEntryRarityRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityReference = optionalRogueLootTableEntryRarityRegistry.get().get(identifier);
			if (optionalRogueLootTableEntryRarityReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootTableEntryRarityReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
