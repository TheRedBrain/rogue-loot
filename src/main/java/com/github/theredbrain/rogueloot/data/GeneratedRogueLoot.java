package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record GeneratedRogueLoot(
		RogueLootGuiSettings rogueLootGuiSettings,
		List<VisibleRogueLootTableEntry> entryList,
		int minChoices,
		int maxChoices,
		boolean enableRerolling,
		Optional<ItemStackTemplate> rerollCost,
		boolean enableEntryBanishing,
		Optional<ItemStackTemplate> banishingCost,
		boolean enableEntryReserving,
		Optional<ItemStackTemplate> reservingCost
) {
	public static final Codec<GeneratedRogueLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RogueLootGuiSettings.CODEC.fieldOf("rogue_loot_gui_settings").forGetter(x -> x.rogueLootGuiSettings),
			VisibleRogueLootTableEntry.CODEC.listOf().fieldOf("entry_list").forGetter(x -> x.entryList),
			Codec.INT.fieldOf("min_choices").forGetter(x -> x.minChoices),
			Codec.INT.fieldOf("max_choices").forGetter(x -> x.maxChoices),
			Codec.BOOL.fieldOf("enable_rerolling").forGetter(x -> x.enableRerolling),
			ItemStackTemplate.CODEC.optionalFieldOf("reroll_cost").forGetter(x -> x.rerollCost),
			Codec.BOOL.fieldOf("enable_entry_banishing").forGetter(x -> x.enableEntryBanishing),
			ItemStackTemplate.CODEC.optionalFieldOf("banishing_cost").forGetter(x -> x.banishingCost),
			Codec.BOOL.optionalFieldOf("enable_entry_reserving", false).forGetter(x -> x.enableEntryReserving),
			ItemStackTemplate.CODEC.optionalFieldOf("reserving_cost").forGetter(x -> x.reservingCost)
	).apply(instance, GeneratedRogueLoot::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLoot> STREAM_CODEC = StreamCodec.ofMember(GeneratedRogueLoot::write, GeneratedRogueLoot::new);

	public GeneratedRogueLoot(final RegistryFriendlyByteBuf registryFriendlyByteBuf) {
		this(
				RogueLootGuiSettings.STREAM_CODEC.decode(registryFriendlyByteBuf),
				registryFriendlyByteBuf.readList(VisibleRogueLootTableEntry.STREAM_CODEC),
				ByteBufCodecs.INT.decode(registryFriendlyByteBuf),
				ByteBufCodecs.INT.decode(registryFriendlyByteBuf),
				ByteBufCodecs.BOOL.decode(registryFriendlyByteBuf),
				ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(registryFriendlyByteBuf),
				ByteBufCodecs.BOOL.decode(registryFriendlyByteBuf),
				ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(registryFriendlyByteBuf),
				ByteBufCodecs.BOOL.decode(registryFriendlyByteBuf),
				ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(registryFriendlyByteBuf)
		);
	}

	private void write(final FriendlyByteBuf friendlyByteBuf) {
		RogueLootGuiSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.rogueLootGuiSettings);
		friendlyByteBuf.writeCollection(this.entryList, VisibleRogueLootTableEntry.STREAM_CODEC);
		ByteBufCodecs.INT.encode(friendlyByteBuf, this.minChoices);
		ByteBufCodecs.INT.encode(friendlyByteBuf, this.maxChoices);
		ByteBufCodecs.BOOL.encode(friendlyByteBuf, this.enableRerolling);
		ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.rerollCost);
		ByteBufCodecs.BOOL.encode(friendlyByteBuf, this.enableEntryBanishing);
		ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.banishingCost);
		ByteBufCodecs.BOOL.encode(friendlyByteBuf, this.enableEntryReserving);
		ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.reservingCost);
	}

	public static GeneratedRogueLoot copy(final GeneratedRogueLoot generatedRogueLoot) {
		List<VisibleRogueLootTableEntry> newVisibleRogueLootTableEntryList = new ArrayList<>();
		generatedRogueLoot.entryList().forEach((visibleRogueLootTableEntry -> {
			newVisibleRogueLootTableEntryList.add(VisibleRogueLootTableEntry.copy(visibleRogueLootTableEntry));
		}));
		return new GeneratedRogueLoot(
				RogueLootGuiSettings.copy(generatedRogueLoot.rogueLootGuiSettings()),
				newVisibleRogueLootTableEntryList,
				generatedRogueLoot.minChoices(),
				generatedRogueLoot.maxChoices(),
				generatedRogueLoot.enableRerolling(),
				generatedRogueLoot.rerollCost().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(generatedRogueLoot.rerollCost().get().create())) : Optional.empty(),
				generatedRogueLoot.enableEntryBanishing(),
				generatedRogueLoot.banishingCost().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(generatedRogueLoot.banishingCost().get().create())) : Optional.empty(),
				generatedRogueLoot.enableEntryReserving(),
				generatedRogueLoot.reservingCost().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(generatedRogueLoot.reservingCost().get().create())) : Optional.empty()
		);
	}

	public record VisibleRogueLootTableEntry(
			Optional<ItemStackTemplate> displayStack,
			Optional<Identifier> iconTextureIdentifier,
			String labelText,
			int usesLeft,
			boolean allowBanishing,
			boolean allowReserving,
			RogueLootTableEntryRarity rogueLootTableEntryRarity,
			Optional<Identifier> parentRogueLootTableEntryId
	) {
		public static final Codec<VisibleRogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStackTemplate.CODEC.optionalFieldOf("display_stack").forGetter(x -> x.displayStack),
				Identifier.CODEC.optionalFieldOf("icon_texture_identifier").forGetter(x -> x.iconTextureIdentifier),
				Codec.STRING.fieldOf("label_text").forGetter(x -> x.labelText),
				Codec.INT.fieldOf("uses_left").forGetter(x -> x.usesLeft),
				Codec.BOOL.fieldOf("allow_banishing").forGetter(x -> x.allowBanishing),
				Codec.BOOL.fieldOf("allow_reserving").forGetter(x -> x.allowReserving),
				RogueLootTableEntryRarity.CODEC.fieldOf("rogue_loot_table_entry_rarity").forGetter(x -> x.rogueLootTableEntryRarity),
				Identifier.CODEC.optionalFieldOf("parent_rogue_loot_table_entry_id").forGetter(x -> x.parentRogueLootTableEntryId)
		).apply(instance, VisibleRogueLootTableEntry::new));

		public static final StreamCodec<FriendlyByteBuf, VisibleRogueLootTableEntry> STREAM_CODEC = StreamCodec.ofMember(VisibleRogueLootTableEntry::write, VisibleRogueLootTableEntry::new);

		public VisibleRogueLootTableEntry(final FriendlyByteBuf friendlyByteBuf) {
			this(
					ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode((RegistryFriendlyByteBuf) friendlyByteBuf),
					ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(friendlyByteBuf),
					ByteBufCodecs.STRING_UTF8.decode(friendlyByteBuf),
					ByteBufCodecs.INT.decode(friendlyByteBuf),
					ByteBufCodecs.BOOL.decode(friendlyByteBuf),
					ByteBufCodecs.BOOL.decode(friendlyByteBuf),
					RogueLootTableEntryRarity.STREAM_CODEC.decode((RegistryFriendlyByteBuf) friendlyByteBuf),
					ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(friendlyByteBuf)
			);
		}

		private void write(final ByteBuf registryByteBuf) {
			ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.displayStack);
			ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(registryByteBuf, this.iconTextureIdentifier);
			ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.labelText);
			ByteBufCodecs.INT.encode(registryByteBuf, this.usesLeft);
			ByteBufCodecs.BOOL.encode(registryByteBuf, this.allowBanishing);
			ByteBufCodecs.BOOL.encode(registryByteBuf, this.allowReserving);
			RogueLootTableEntryRarity.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.rogueLootTableEntryRarity);
			ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(registryByteBuf, this.parentRogueLootTableEntryId);
		}

		public static VisibleRogueLootTableEntry copy(final VisibleRogueLootTableEntry visibleRogueLootTableEntry) {
			return new VisibleRogueLootTableEntry(
					visibleRogueLootTableEntry.displayStack().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(visibleRogueLootTableEntry.displayStack().get().create())) : Optional.empty(),
					visibleRogueLootTableEntry.iconTextureIdentifier(),
					visibleRogueLootTableEntry.labelText(),
					visibleRogueLootTableEntry.usesLeft(),
					visibleRogueLootTableEntry.allowBanishing(),
					visibleRogueLootTableEntry.allowReserving(),
					visibleRogueLootTableEntry.rogueLootTableEntryRarity(),
					visibleRogueLootTableEntry.parentRogueLootTableEntryId()
			);
		}
	}

}
