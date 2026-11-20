package com.github.theredbrain.rogueloot.data.attached;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record RogueLootEntryUsesAttachedData(
		Map<Identifier, Integer> rogueLootEntryUses
) {
	public static RogueLootEntryUsesAttachedData DEFAULT = new RogueLootEntryUsesAttachedData(new HashMap<>());
	public static final Codec<RogueLootEntryUsesAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Identifier.CODEC, Codec.INT).fieldOf("rogue_loot_entry_uses").forGetter(x -> x.rogueLootEntryUses)
	).apply(instance, RogueLootEntryUsesAttachedData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootEntryUsesAttachedData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.INT),
			RogueLootEntryUsesAttachedData::rogueLootEntryUses,
			RogueLootEntryUsesAttachedData::new
	);

	public static RogueLootEntryUsesAttachedData copy(final RogueLootEntryUsesAttachedData rogueLootEntryUsesAttachedData) {
		return new RogueLootEntryUsesAttachedData(new HashMap<>(rogueLootEntryUsesAttachedData.rogueLootEntryUses()));
	}
}
