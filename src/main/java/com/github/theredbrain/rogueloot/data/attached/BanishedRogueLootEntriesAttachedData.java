package com.github.theredbrain.rogueloot.data.attached;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record BanishedRogueLootEntriesAttachedData(List<Identifier> banishedRogueLootEntries) {
	public static BanishedRogueLootEntriesAttachedData DEFAULT = new BanishedRogueLootEntriesAttachedData(new ArrayList<>());
	public static final Codec<BanishedRogueLootEntriesAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("banished_rogue_loot_entries").forGetter(x -> x.banishedRogueLootEntries)
	).apply(instance, BanishedRogueLootEntriesAttachedData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, BanishedRogueLootEntriesAttachedData> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
			BanishedRogueLootEntriesAttachedData::banishedRogueLootEntries,
			BanishedRogueLootEntriesAttachedData::new
	);

	public static BanishedRogueLootEntriesAttachedData copy(final BanishedRogueLootEntriesAttachedData banishedRogueLootEntriesAttachedData) {
		return new BanishedRogueLootEntriesAttachedData(new ArrayList<>(banishedRogueLootEntriesAttachedData.banishedRogueLootEntries()));
	}
}
