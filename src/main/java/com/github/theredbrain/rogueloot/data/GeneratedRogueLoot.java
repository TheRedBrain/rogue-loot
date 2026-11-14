package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record GeneratedRogueLoot(
		String backgroundTexturePath,
		List<ChatFormatting> requirementsNotMetFormatting,
		List<ChatFormatting> requirementsMetFormatting,
		List<VisibleRogueLootTableEntry> entryList,
		int minChoices,
		int maxChoices,
		boolean enableEntryBanishing,
		Optional<ItemStackTemplate> banishingCost
) {
	public static final Codec<GeneratedRogueLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("background_texture_path").forGetter(x -> x.backgroundTexturePath),
			ChatFormatting.CODEC.listOf().fieldOf("requirements_not_met_formatting").forGetter(x -> x.requirementsNotMetFormatting),
			ChatFormatting.CODEC.listOf().fieldOf("requirements_met_formatting").forGetter(x -> x.requirementsMetFormatting),
			VisibleRogueLootTableEntry.CODEC.listOf().fieldOf("entry_list").forGetter(x -> x.entryList),
			Codec.INT.fieldOf("min_choices").forGetter(x -> x.minChoices),
			Codec.INT.fieldOf("max_choices").forGetter(x -> x.maxChoices),
			Codec.BOOL.fieldOf("enable_entry_banishing").forGetter(x -> x.enableEntryBanishing),
			ItemStackTemplate.CODEC.optionalFieldOf("banishing_cost").forGetter(x -> x.banishingCost)
	).apply(instance, GeneratedRogueLoot::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLoot> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			GeneratedRogueLoot::backgroundTexturePath,
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
			ByteBufCodecs.BOOL,
			GeneratedRogueLoot::enableEntryBanishing,
			ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
			GeneratedRogueLoot::banishingCost,
			GeneratedRogueLoot::new
	);

	public GeneratedRogueLoot(
			String backgroundTexturePath,
			List<ChatFormatting> requirementsNotMetFormatting,
			List<ChatFormatting> requirementsMetFormatting,
			List<VisibleRogueLootTableEntry> entryList,
			int minChoices,
			int maxChoices,
			boolean enableEntryBanishing,
			Optional<ItemStackTemplate> banishingCost
	) {
		this.backgroundTexturePath = backgroundTexturePath != null ? backgroundTexturePath : "";
		this.requirementsNotMetFormatting = requirementsNotMetFormatting != null ? requirementsNotMetFormatting : List.of();
		this.requirementsMetFormatting = requirementsMetFormatting != null ? requirementsMetFormatting : List.of();
		this.entryList = entryList != null ? entryList : List.of();
		this.minChoices = minChoices;
		this.maxChoices = maxChoices;
		this.enableEntryBanishing = enableEntryBanishing;
		this.banishingCost = banishingCost;
	}

	public static GeneratedRogueLoot copy(GeneratedRogueLoot generatedRogueLoot) {
		List<VisibleRogueLootTableEntry> newVisibleRogueLootTableEntryList = new ArrayList<>();
		generatedRogueLoot.entryList().forEach((visibleRogueLootTableEntry -> {
			newVisibleRogueLootTableEntryList.add(VisibleRogueLootTableEntry.copy(visibleRogueLootTableEntry));
		}));
		return new GeneratedRogueLoot(
				generatedRogueLoot.backgroundTexturePath(),
				new ArrayList<>(generatedRogueLoot.requirementsNotMetFormatting()),
				new ArrayList<>(generatedRogueLoot.requirementsMetFormatting()),
				newVisibleRogueLootTableEntryList,
				generatedRogueLoot.minChoices(),
				generatedRogueLoot.maxChoices(),
				generatedRogueLoot.enableEntryBanishing(),
				generatedRogueLoot.banishingCost().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(generatedRogueLoot.banishingCost().get().create())) : Optional.empty()
		);
	}

	public record VisibleRogueLootTableEntry(
			String entryBackgroundTexturePathAddition,
			Optional<ItemStackTemplate> displayStack,
			Optional<Identifier> iconTextureIdentifier,
			String labelText,
			List<ChatFormatting> labelFormatting,
			boolean allowBanishing,
			Identifier parentRogueLootTableEntryId
	) {
		public static final Codec<VisibleRogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("entry_background_texture_path_addition").forGetter(x -> x.entryBackgroundTexturePathAddition),
				ItemStackTemplate.CODEC.optionalFieldOf("display_stack").forGetter(x -> x.displayStack),
				Identifier.CODEC.optionalFieldOf("icon_texture_identifier").forGetter(x -> x.iconTextureIdentifier),
				Codec.STRING.fieldOf("label_text").forGetter(x -> x.labelText),
				ChatFormatting.CODEC.listOf().fieldOf("label_formatting").forGetter(x -> x.labelFormatting),
				Codec.BOOL.fieldOf("allow_banishing").forGetter(x -> x.allowBanishing),
				Identifier.CODEC.fieldOf("parent_rogue_loot_table_entry_id").forGetter(x -> x.parentRogueLootTableEntryId)
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
				ByteBufCodecs.BOOL,
				VisibleRogueLootTableEntry::allowBanishing,
				Identifier.STREAM_CODEC,
				VisibleRogueLootTableEntry::parentRogueLootTableEntryId,
				VisibleRogueLootTableEntry::new
		);

		public VisibleRogueLootTableEntry(
				String entryBackgroundTexturePathAddition,
				Optional<ItemStackTemplate> displayStack,
				Optional<Identifier> iconTextureIdentifier,
				String labelText,
				List<ChatFormatting> labelFormatting,
				boolean allowBanishing,
				Identifier parentRogueLootTableEntryId
		) {
			this.entryBackgroundTexturePathAddition = entryBackgroundTexturePathAddition != null ? entryBackgroundTexturePathAddition : "";
			this.displayStack = displayStack;
			this.iconTextureIdentifier = iconTextureIdentifier;
			this.labelText = labelText != null ? labelText : "";
			this.labelFormatting = labelFormatting != null ? labelFormatting : List.of();
			this.allowBanishing = allowBanishing;
			this.parentRogueLootTableEntryId = parentRogueLootTableEntryId;
		}

		public static VisibleRogueLootTableEntry copy(VisibleRogueLootTableEntry visibleRogueLootTableEntry) {
			return new VisibleRogueLootTableEntry(
					visibleRogueLootTableEntry.entryBackgroundTexturePathAddition(),
					visibleRogueLootTableEntry.displayStack().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(visibleRogueLootTableEntry.displayStack().get().create())) : Optional.empty(),
					visibleRogueLootTableEntry.iconTextureIdentifier(),
					visibleRogueLootTableEntry.labelText(),
					new ArrayList<>(visibleRogueLootTableEntry.labelFormatting()),
					visibleRogueLootTableEntry.allowBanishing(),
					visibleRogueLootTableEntry.parentRogueLootTableEntryId()
			);
		}
	}

}
