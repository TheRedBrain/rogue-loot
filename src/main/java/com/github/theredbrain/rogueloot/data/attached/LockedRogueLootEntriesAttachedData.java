package com.github.theredbrain.rogueloot.data.attached;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record LockedRogueLootEntriesAttachedData(List<Identifier> lockedRogueLootEntries) {
	public static LockedRogueLootEntriesAttachedData DEFAULT = new LockedRogueLootEntriesAttachedData(new ArrayList<>());
	public static final Codec<LockedRogueLootEntriesAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("locked_rogue_loot_entries").forGetter(x -> x.lockedRogueLootEntries)
	).apply(instance, LockedRogueLootEntriesAttachedData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LockedRogueLootEntriesAttachedData> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
			LockedRogueLootEntriesAttachedData::lockedRogueLootEntries,
			LockedRogueLootEntriesAttachedData::new
	);

	public static LockedRogueLootEntriesAttachedData copy(final LockedRogueLootEntriesAttachedData lockedRogueLootEntriesAttachedData) {
		return new LockedRogueLootEntriesAttachedData(new ArrayList<>(lockedRogueLootEntriesAttachedData.lockedRogueLootEntries()));
	}
}
