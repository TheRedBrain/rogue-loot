package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
		Optional<CommonDataStructures.RogueLootTableInteraction> rerolling,
		Optional<CommonDataStructures.RogueLootTableInteraction> banishing,
		Optional<CommonDataStructures.RogueLootTableInteraction> reserving
) {
	public static final Codec<GeneratedRogueLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RogueLootGuiSettings.CODEC.fieldOf("rogue_loot_gui_settings").forGetter(x -> x.rogueLootGuiSettings),
			VisibleRogueLootTableEntry.CODEC.listOf().fieldOf("entry_list").forGetter(x -> x.entryList),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("reroll_cost").forGetter(x -> x.rerolling),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("banishing_cost").forGetter(x -> x.banishing),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("reserving_cost").forGetter(x -> x.reserving)
	).apply(instance, GeneratedRogueLoot::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLoot> STREAM_CODEC = StreamCodec.ofMember(GeneratedRogueLoot::write, GeneratedRogueLoot::new);

	public GeneratedRogueLoot(final RegistryFriendlyByteBuf registryFriendlyByteBuf) {
		this(
				RogueLootGuiSettings.STREAM_CODEC.decode(registryFriendlyByteBuf),
				registryFriendlyByteBuf.readList(VisibleRogueLootTableEntry.STREAM_CODEC),
				ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).decode(registryFriendlyByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).decode(registryFriendlyByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).decode(registryFriendlyByteBuf)
		);
	}

	private void write(final FriendlyByteBuf friendlyByteBuf) {
		RogueLootGuiSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.rogueLootGuiSettings);
		friendlyByteBuf.writeCollection(this.entryList, VisibleRogueLootTableEntry.STREAM_CODEC);
		ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.rerolling);
		ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.banishing);
		ByteBufCodecs.optional(CommonDataStructures.RogueLootTableInteraction.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.reserving);
	}

	public static GeneratedRogueLoot copy(final GeneratedRogueLoot generatedRogueLoot) {
		List<VisibleRogueLootTableEntry> newVisibleRogueLootTableEntryList = new ArrayList<>();
		generatedRogueLoot.entryList().forEach((visibleRogueLootTableEntry -> {
			newVisibleRogueLootTableEntryList.add(VisibleRogueLootTableEntry.copy(visibleRogueLootTableEntry));
		}));
		return new GeneratedRogueLoot(
				RogueLootGuiSettings.copy(generatedRogueLoot.rogueLootGuiSettings()),
				newVisibleRogueLootTableEntryList,
				generatedRogueLoot.rerolling(),
				generatedRogueLoot.banishing(),
				generatedRogueLoot.reserving()
		);
	}

	public record VisibleRogueLootTableEntry(
			Optional<CommonDataStructures.RogueLootTableEntryDisplay> display,
			Optional<CommonDataStructures.RogueLootTableEntryTooltip> tooltip,
			int usesLeft,
			boolean allowBanishing,
			boolean allowReserving,
			Optional<RogueLootTableEntryRarity> rogueLootTableEntryRarity,
			Optional<Identifier> parentRogueLootTableEntryId
	) {
		public static final Codec<VisibleRogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				CommonDataStructures.RogueLootTableEntryDisplay.CODEC.optionalFieldOf("display").forGetter(x -> x.display),
				CommonDataStructures.RogueLootTableEntryTooltip.CODEC.optionalFieldOf("tooltip").forGetter(x -> x.tooltip),
				Codec.INT.fieldOf("uses_left").forGetter(x -> x.usesLeft),
				Codec.BOOL.fieldOf("allow_banishing").forGetter(x -> x.allowBanishing),
				Codec.BOOL.fieldOf("allow_reserving").forGetter(x -> x.allowReserving),
				RogueLootTableEntryRarity.CODEC.optionalFieldOf("rogue_loot_table_entry_rarity").forGetter(x -> x.rogueLootTableEntryRarity),
				Identifier.CODEC.optionalFieldOf("parent_rogue_loot_table_entry_id").forGetter(x -> x.parentRogueLootTableEntryId)
		).apply(instance, VisibleRogueLootTableEntry::new));

		public static final StreamCodec<FriendlyByteBuf, VisibleRogueLootTableEntry> STREAM_CODEC = StreamCodec.ofMember(VisibleRogueLootTableEntry::write, VisibleRogueLootTableEntry::new);

		public VisibleRogueLootTableEntry(final FriendlyByteBuf friendlyByteBuf) {
			this(
					ByteBufCodecs.optional(CommonDataStructures.RogueLootTableEntryDisplay.STREAM_CODEC).decode(friendlyByteBuf),
					ByteBufCodecs.optional(CommonDataStructures.RogueLootTableEntryTooltip.STREAM_CODEC).decode(friendlyByteBuf),
					ByteBufCodecs.INT.decode(friendlyByteBuf),
					ByteBufCodecs.BOOL.decode(friendlyByteBuf),
					ByteBufCodecs.BOOL.decode(friendlyByteBuf),
					ByteBufCodecs.optional(RogueLootTableEntryRarity.STREAM_CODEC).decode((RegistryFriendlyByteBuf) friendlyByteBuf),
					ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(friendlyByteBuf)
			);
		}

		private void write(final FriendlyByteBuf friendlyByteBuf) {
			ByteBufCodecs.optional(CommonDataStructures.RogueLootTableEntryDisplay.STREAM_CODEC).encode(friendlyByteBuf, this.display);
			ByteBufCodecs.optional(CommonDataStructures.RogueLootTableEntryTooltip.STREAM_CODEC).encode(friendlyByteBuf, this.tooltip);
			ByteBufCodecs.INT.encode(friendlyByteBuf, this.usesLeft);
			ByteBufCodecs.BOOL.encode(friendlyByteBuf, this.allowBanishing);
			ByteBufCodecs.BOOL.encode(friendlyByteBuf, this.allowReserving);
			ByteBufCodecs.optional(RogueLootTableEntryRarity.STREAM_CODEC).encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.rogueLootTableEntryRarity);
			ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(friendlyByteBuf, this.parentRogueLootTableEntryId);
		}

		public static VisibleRogueLootTableEntry copy(final VisibleRogueLootTableEntry visibleRogueLootTableEntry) {
			return new VisibleRogueLootTableEntry(
					visibleRogueLootTableEntry.display().isPresent() ? Optional.of(CommonDataStructures.RogueLootTableEntryDisplay.copy(visibleRogueLootTableEntry.display().get())) : Optional.empty(),
					visibleRogueLootTableEntry.tooltip().isPresent() ? Optional.of(CommonDataStructures.RogueLootTableEntryTooltip.copy(visibleRogueLootTableEntry.tooltip().get())) : Optional.empty(),
					visibleRogueLootTableEntry.usesLeft(),
					visibleRogueLootTableEntry.allowBanishing(),
					visibleRogueLootTableEntry.allowReserving(),
					visibleRogueLootTableEntry.rogueLootTableEntryRarity(),
					visibleRogueLootTableEntry.parentRogueLootTableEntryId()
			);
		}
	}

}
