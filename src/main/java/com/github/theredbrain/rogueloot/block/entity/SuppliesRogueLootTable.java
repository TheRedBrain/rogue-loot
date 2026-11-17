package com.github.theredbrain.rogueloot.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SuppliesRogueLootTable {
	List<Identifier> getGeneratedRogueLootEntries(final ServerPlayer serverPlayer);

	void setGeneratedRogueLootEntries(final List<Identifier> generatedRogueLootEntries, final ServerPlayer serverPlayer);

	void removeGeneratedRogueLootEntries(final ServerPlayer serverPlayer);

	record GeneratedRogueLootEntriesForPlayer(
			Map<UUID, GeneratedRogueLootEntries> generatedRogueLootEntries
	) {
		public static GeneratedRogueLootEntriesForPlayer DEFAULT = new GeneratedRogueLootEntriesForPlayer(new HashMap<>());
		public static final Codec<GeneratedRogueLootEntriesForPlayer> CODEC = Codec.unboundedMap(UUIDUtil.AUTHLIB_CODEC, GeneratedRogueLootEntries.CODEC)
				.xmap(
						map -> new GeneratedRogueLootEntriesForPlayer(new HashMap<>(map)),
						generatedRogueLootEntries -> generatedRogueLootEntries.generatedRogueLootEntries
				);
		public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootEntriesForPlayer> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, GeneratedRogueLootEntries.STREAM_CODEC),
				GeneratedRogueLootEntriesForPlayer::generatedRogueLootEntries,
				GeneratedRogueLootEntriesForPlayer::new
		);

		public static GeneratedRogueLootEntriesForPlayer copy(final GeneratedRogueLootEntriesForPlayer generatedRogueLootEntriesForPlayer) {
			Map<UUID, GeneratedRogueLootEntries> newGeneratedRogueLootEntriesForPlayer = new HashMap<>();
			generatedRogueLootEntriesForPlayer.generatedRogueLootEntries().forEach((uuid, generatedRogueLootEntries) -> {
				newGeneratedRogueLootEntriesForPlayer.put(uuid, GeneratedRogueLootEntries.copy(generatedRogueLootEntries));
			});
			return new GeneratedRogueLootEntriesForPlayer(newGeneratedRogueLootEntriesForPlayer);
		}

		record GeneratedRogueLootEntries(
				List<Identifier> generatedRogueLootEntries
		) {
			public static SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries DEFAULT = new SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries(new ArrayList<>());
			public static final Codec<GeneratedRogueLootEntries> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Identifier.CODEC.listOf().fieldOf("generated_rogue_loot_entries").forGetter(x -> x.generatedRogueLootEntries)
			).apply(instance, GeneratedRogueLootEntries::new));

			public static final StreamCodec<RegistryFriendlyByteBuf, SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries> STREAM_CODEC = StreamCodec.composite(
					Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
					SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries::generatedRogueLootEntries,
					SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries::new
			);

			public static SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries copy(final SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries generatedRogueLootEntries) {
				return new SuppliesRogueLootTable.GeneratedRogueLootEntriesForPlayer.GeneratedRogueLootEntries(new ArrayList<>(generatedRogueLootEntries.generatedRogueLootEntries()));
			}

		}
	}
}
