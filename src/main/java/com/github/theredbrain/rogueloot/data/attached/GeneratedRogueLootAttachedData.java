package com.github.theredbrain.rogueloot.data.attached;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record GeneratedRogueLootAttachedData(
		Map<Identifier, GeneratedRogueLootEntries> generatedRogueLootData
) {
	public static GeneratedRogueLootAttachedData DEFAULT = new GeneratedRogueLootAttachedData(new HashMap<>());
	public static final Codec<GeneratedRogueLootAttachedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Identifier.CODEC, GeneratedRogueLootEntries.CODEC).fieldOf("generated_rogue_loot_data").forGetter(x -> x.generatedRogueLootData)
	).apply(instance, GeneratedRogueLootAttachedData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootAttachedData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, GeneratedRogueLootEntries.STREAM_CODEC),
			GeneratedRogueLootAttachedData::generatedRogueLootData,
			GeneratedRogueLootAttachedData::new
	);

	public static GeneratedRogueLootAttachedData copy(final GeneratedRogueLootAttachedData generatedRogueLootAttachedData) {
		Map<Identifier, GeneratedRogueLootEntries> newGeneratedRogueLootData = new HashMap<>();
		generatedRogueLootAttachedData.generatedRogueLootData().forEach((generatedRogueLootTableIdentifier, generatedRogueLootEntries) -> {
			newGeneratedRogueLootData.put(generatedRogueLootTableIdentifier, GeneratedRogueLootEntries.copy(generatedRogueLootEntries));
		});
		return new GeneratedRogueLootAttachedData(newGeneratedRogueLootData);
	}

	public record GeneratedRogueLootEntries(
			List<Identifier> generatedRogueLootEntries
	) {
		public static GeneratedRogueLootEntries DEFAULT = new GeneratedRogueLootEntries(new ArrayList<>());
		public static final Codec<GeneratedRogueLootEntries> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Identifier.CODEC.listOf().fieldOf("generated_rogue_loot_entries").forGetter(x -> x.generatedRogueLootEntries)
		).apply(instance, GeneratedRogueLootEntries::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootEntries> STREAM_CODEC = StreamCodec.composite(
				Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
				GeneratedRogueLootEntries::generatedRogueLootEntries,
				GeneratedRogueLootEntries::new
		);

		public static GeneratedRogueLootEntries copy(final GeneratedRogueLootEntries generatedRogueLootEntries) {
			return new GeneratedRogueLootEntries(new ArrayList<>(generatedRogueLootEntries.generatedRogueLootEntries()));
		}

	}
}
