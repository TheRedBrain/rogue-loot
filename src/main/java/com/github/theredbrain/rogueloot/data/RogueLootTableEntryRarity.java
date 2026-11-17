package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootDynamicRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record RogueLootTableEntryRarity(
		String assetPathAddition,
		int defaultWeight,
		List<ChatFormatting> labelFormatting
) {
	public static final Codec<RogueLootTableEntryRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("asset_path_addition", "").forGetter(x -> x.assetPathAddition),
			Codec.INT.optionalFieldOf("default_weight", 1).forGetter(x -> x.defaultWeight),
			ChatFormatting.CODEC.listOf().optionalFieldOf("label_formatting", List.of()).forGetter(x -> x.labelFormatting)
	).apply(instance, RogueLootTableEntryRarity::new));

	public RogueLootTableEntryRarity(
			final String assetPathAddition,
			final int defaultWeight,
			final List<ChatFormatting> labelFormatting
	) {
		this.assetPathAddition = assetPathAddition != null ? assetPathAddition : "";
		this.defaultWeight = defaultWeight;
		this.labelFormatting = labelFormatting != null ? labelFormatting : List.of();
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
