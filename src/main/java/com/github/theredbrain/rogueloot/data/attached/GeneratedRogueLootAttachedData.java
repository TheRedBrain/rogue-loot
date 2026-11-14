package com.github.theredbrain.rogueloot.data.attached;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record GeneratedRogueLootAttachedData(
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> generatedRogueLootData
) {
	public static GeneratedRogueLootAttachedData DEFAULT = new GeneratedRogueLootAttachedData(new HashMap<>());
	public static final Codec<GeneratedRogueLootAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Identifier.CODEC, Codec.unboundedMap(UUIDUtil.CODEC, GeneratedRogueLoot.CODEC)).fieldOf("generated_rogue_loot_data").forGetter(x -> x.generatedRogueLootData)
	).apply(instance, GeneratedRogueLootAttachedData::new));
//	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootAttachedData> STREAM_CODEC = StreamCodec.composite(
//			ByteBufCodecs.map(Object2ObjectOpenHashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.map(Object2ObjectOpenHashMap::new, UUIDUtil.STREAM_CODEC, GeneratedRogueLoot.STREAM_CODEC)),
//			GeneratedRogueLootAttachedData::generatedRogueLootData,
//			GeneratedRogueLootAttachedData::new
//	);

	public static GeneratedRogueLootAttachedData copy(final GeneratedRogueLootAttachedData generatedRogueLootAttachedData) {
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> newGeneratedRogueLootData = new HashMap<>();
		generatedRogueLootAttachedData.generatedRogueLootData().forEach((identifier, map) -> {
			Map<UUID, GeneratedRogueLoot> newMap = new HashMap<>();
			map.forEach(((uuid, generatedRogueLoot) -> {
				newMap.put(uuid, GeneratedRogueLoot.copy(generatedRogueLoot));
			}));
			newGeneratedRogueLootData.put(identifier, newMap);
		});
		return new GeneratedRogueLootAttachedData(newGeneratedRogueLootData);
	}
}
