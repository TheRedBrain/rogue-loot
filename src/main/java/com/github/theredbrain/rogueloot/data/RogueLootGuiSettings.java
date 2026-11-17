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

public record RogueLootGuiSettings(
		String backgroundTexturePath,
		String entriesAssetPathAddition,
		List<ChatFormatting> requirementsNotMetFormatting,
		List<ChatFormatting> requirementsMetFormatting
) {
	public static final RogueLootGuiSettings DEFAULT = new RogueLootGuiSettings("", "", List.of(), List.of());
	public static final Codec<RogueLootGuiSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("background_texture_path", "").forGetter(x -> x.backgroundTexturePath),
			Codec.STRING.optionalFieldOf("entries_asset_path_addition", "").forGetter(x -> x.entriesAssetPathAddition),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_not_met_formatting", List.of()).forGetter(x -> x.requirementsNotMetFormatting),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_met_formatting", List.of()).forGetter(x -> x.requirementsMetFormatting)
	).apply(instance, RogueLootGuiSettings::new));

	public RogueLootGuiSettings(
			final String backgroundTexturePath,
			final String entriesAssetPathAddition,
			final List<ChatFormatting> requirementsNotMetFormatting,
			final List<ChatFormatting> requirementsMetFormatting
	) {
		this.backgroundTexturePath = backgroundTexturePath != null ? backgroundTexturePath : "";
		this.entriesAssetPathAddition = entriesAssetPathAddition != null ? entriesAssetPathAddition : "";
		this.requirementsNotMetFormatting = requirementsNotMetFormatting != null ? requirementsNotMetFormatting : List.of();
		this.requirementsMetFormatting = requirementsMetFormatting != null ? requirementsMetFormatting : List.of();
	}

	@Nullable
	public static RogueLootGuiSettings getRogueLootGuiSettings(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootGuiSettings rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootGuiSettings>> optionalRogueLootGuiSettingsRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY);
		if (optionalRogueLootGuiSettingsRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootGuiSettings>> optionalRogueLootGuiSettingsReference = optionalRogueLootGuiSettingsRegistry.get().get(identifier);
			if (optionalRogueLootGuiSettingsReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootGuiSettingsReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
