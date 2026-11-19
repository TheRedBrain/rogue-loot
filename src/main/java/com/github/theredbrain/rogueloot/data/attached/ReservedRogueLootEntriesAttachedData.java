package com.github.theredbrain.rogueloot.data.attached;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record ReservedRogueLootEntriesAttachedData(List<Identifier> reservedRogueLootEntries) {
	public static ReservedRogueLootEntriesAttachedData DEFAULT = new ReservedRogueLootEntriesAttachedData(new ArrayList<>());
	public static final Codec<ReservedRogueLootEntriesAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("reserved_rogue_loot_entries").forGetter(x -> x.reservedRogueLootEntries)
	).apply(instance, ReservedRogueLootEntriesAttachedData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ReservedRogueLootEntriesAttachedData> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
			ReservedRogueLootEntriesAttachedData::reservedRogueLootEntries,
			ReservedRogueLootEntriesAttachedData::new
	);

	public static ReservedRogueLootEntriesAttachedData copy(final ReservedRogueLootEntriesAttachedData reservedRogueLootEntriesAttachedData) {
		return new ReservedRogueLootEntriesAttachedData(new ArrayList<>(reservedRogueLootEntriesAttachedData.reservedRogueLootEntries()));
	}
}
