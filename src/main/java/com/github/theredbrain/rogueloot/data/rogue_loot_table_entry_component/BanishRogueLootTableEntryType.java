package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public record BanishRogueLootTableEntryType(
		HolderSet<RogueLootTableEntry> entries
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<BanishRogueLootTableEntryType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							RegistryCodecs.homogeneousList(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY).fieldOf("rogue_loot_table_entries").forGetter(x -> x.entries)
					)
					.apply(instance, BanishRogueLootTableEntryType::new)
	);

	@Override
	public MapCodec<BanishRogueLootTableEntryType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		for (Holder<RogueLootTableEntry> entry : this.entries) {
			Optional<ResourceKey<RogueLootTableEntry>> optionalResourceKey = entry.unwrapKey();
			if (optionalResourceKey.isEmpty()) {
				continue;
			}
			Identifier entryIdentifier = optionalResourceKey.get().identifier();
			DataAttachmentHelper.banishRogueLootEntry(serverPlayer, entryIdentifier);
		}
	}
}
