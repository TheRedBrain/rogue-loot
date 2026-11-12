package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record GeneratedRogueLoot(
		String backGroundTexturePath,
		List<ChatFormatting> requirementsNotMetFormatting,
		List<ChatFormatting> requirementsMetFormatting,
		List<VisibleRogueLootTableEntry> entryList,
		int minChoices,
		int maxChoices
) {
	public static final Codec<GeneratedRogueLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("", "").forGetter(x -> x.backGroundTexturePath),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_not_met_formatting", List.of()).forGetter(x -> x.requirementsNotMetFormatting),
			ChatFormatting.CODEC.listOf().optionalFieldOf("requirements_met_formatting", List.of()).forGetter(x -> x.requirementsMetFormatting),
			VisibleRogueLootTableEntry.CODEC.listOf().fieldOf("entry_list").forGetter(x -> x.entryList),
			Codec.INT.fieldOf("min_choices").forGetter(x -> x.minChoices),
			Codec.INT.fieldOf("max_choices").forGetter(x -> x.maxChoices)
	).apply(instance, GeneratedRogueLoot::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLoot> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			GeneratedRogueLoot::backGroundTexturePath,
			ByteBufCodecs.fromCodec(ChatFormatting.CODEC).apply(ByteBufCodecs.list()),
			GeneratedRogueLoot::requirementsNotMetFormatting,
			ByteBufCodecs.fromCodec(ChatFormatting.CODEC).apply(ByteBufCodecs.list()),
			GeneratedRogueLoot::requirementsMetFormatting,
			VisibleRogueLootTableEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
			GeneratedRogueLoot::entryList,
			ByteBufCodecs.INT,
			GeneratedRogueLoot::minChoices,
			ByteBufCodecs.INT,
			GeneratedRogueLoot::maxChoices,
			GeneratedRogueLoot::new
	);

	public GeneratedRogueLoot(
			String backGroundTexturePath,
			List<ChatFormatting> requirementsNotMetFormatting,
			List<ChatFormatting> requirementsMetFormatting,
			List<VisibleRogueLootTableEntry> entryList,
			int minChoices,
			int maxChoices
	) {
		this.backGroundTexturePath = backGroundTexturePath != null ? backGroundTexturePath : "";
		this.requirementsNotMetFormatting = requirementsNotMetFormatting != null ? requirementsNotMetFormatting : List.of();
		this.requirementsMetFormatting = requirementsMetFormatting != null ? requirementsMetFormatting : List.of();
		this.entryList = entryList != null ? entryList : List.of();
		this.minChoices = minChoices;
		this.maxChoices = maxChoices;
	}

	public record VisibleRogueLootTableEntry(
			String entryBackgroundTexturePathAddition,
			Optional<ItemStackTemplate> displayStack,
			Optional<Identifier> iconTextureIdentifier,
			String labelText,
			List<ChatFormatting> labelFormatting,
			String parentRogueLootTableEntryId
	) {
		public static final Codec<VisibleRogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("entry_background_texture_path_addition").forGetter(x -> x.entryBackgroundTexturePathAddition),
				ItemStackTemplate.CODEC.optionalFieldOf("display_stack").forGetter(x -> x.displayStack),
				Identifier.CODEC.optionalFieldOf("icon_texture_identifier").forGetter(x -> x.iconTextureIdentifier),
				Codec.STRING.optionalFieldOf("label_text", "").forGetter(x -> x.labelText),
				ChatFormatting.CODEC.listOf().optionalFieldOf("label_formatting", List.of()).forGetter(x -> x.labelFormatting),
				Codec.STRING.fieldOf("parent_rogue_loot_table_entry_id").forGetter(x -> x.parentRogueLootTableEntryId)
		).apply(instance, VisibleRogueLootTableEntry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, VisibleRogueLootTableEntry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				VisibleRogueLootTableEntry::entryBackgroundTexturePathAddition,
				ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
				VisibleRogueLootTableEntry::displayStack,
				ByteBufCodecs.optional(Identifier.STREAM_CODEC),
				VisibleRogueLootTableEntry::iconTextureIdentifier,
				ByteBufCodecs.STRING_UTF8,
				VisibleRogueLootTableEntry::labelText,
				ByteBufCodecs.fromCodec(ChatFormatting.CODEC).apply(ByteBufCodecs.list()),
				VisibleRogueLootTableEntry::labelFormatting,
				ByteBufCodecs.STRING_UTF8,
				VisibleRogueLootTableEntry::parentRogueLootTableEntryId,
				VisibleRogueLootTableEntry::new
		);

		public VisibleRogueLootTableEntry(
				String entryBackgroundTexturePathAddition,
				Optional<ItemStackTemplate> displayStack,
				Optional<Identifier> iconTextureIdentifier,
				String labelText,
				List<ChatFormatting> labelFormatting,
				String parentRogueLootTableEntryId
		) {
			this.entryBackgroundTexturePathAddition = entryBackgroundTexturePathAddition != null ? entryBackgroundTexturePathAddition : "";
			this.displayStack = displayStack;
			this.iconTextureIdentifier = iconTextureIdentifier;
			this.labelText = labelText != null ? labelText : "";
			this.labelFormatting = labelFormatting != null ? labelFormatting : List.of();
			this.parentRogueLootTableEntryId = parentRogueLootTableEntryId != null ? parentRogueLootTableEntryId : "";
		}

	}

}
