package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;

import java.util.List;

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
			String assetPathAddition,
			int defaultWeight,
			List<ChatFormatting> labelFormatting
	) {
		this.assetPathAddition = assetPathAddition != null ? assetPathAddition : "";
		this.defaultWeight = defaultWeight;
		this.labelFormatting = labelFormatting != null ? labelFormatting : List.of();
	}
}
