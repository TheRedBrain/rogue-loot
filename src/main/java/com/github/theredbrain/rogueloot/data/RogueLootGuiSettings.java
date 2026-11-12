package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;

import java.util.List;

public record RogueLootGuiSettings(
		String backgroundTexturePath,
		String entriesAssetPathAddition,
		List<ChatFormatting> requirementsNotMetFormatting,
		List<ChatFormatting> requirementsMetFormatting
) {
	public static final RogueLootGuiSettings DEFAULT = new RogueLootGuiSettings("", "", List.of(), List.of()/*-6250336, true*/);
	public static final Codec<RogueLootGuiSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("background_texture_path", "").forGetter(x -> x.backgroundTexturePath),
			Codec.STRING.optionalFieldOf("entries_asset_path_addition", "").forGetter(x -> x.entriesAssetPathAddition),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_not_met_formatting", List.of()).forGetter(x -> x.requirementsNotMetFormatting),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_met_formatting", List.of()).forGetter(x -> x.requirementsMetFormatting)
	).apply(instance, RogueLootGuiSettings::new));

	public RogueLootGuiSettings(
			String backgroundTexturePath,
			String entriesAssetPathAddition,
			List<ChatFormatting> requirementsNotMetFormatting,
			List<ChatFormatting> requirementsMetFormatting
	) {
		this.backgroundTexturePath = backgroundTexturePath != null ? backgroundTexturePath : "";
		this.entriesAssetPathAddition = entriesAssetPathAddition != null ? entriesAssetPathAddition : "";
		this.requirementsNotMetFormatting = requirementsNotMetFormatting != null ? requirementsNotMetFormatting : List.of();
		this.requirementsMetFormatting = requirementsMetFormatting != null ? requirementsMetFormatting : List.of();
	}
}
