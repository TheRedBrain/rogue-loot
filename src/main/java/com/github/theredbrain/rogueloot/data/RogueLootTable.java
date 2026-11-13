package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.CustomDynamicRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record RogueLootTable(
		HolderSet<RogueLootTableEntry> rogueLootTableEntries,
		Map<Identifier, Integer> rarityOverrides,
		String rogueLootGuiSettingsIdString,
) {
	public static final Codec<RogueLootTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.homogeneousList(CustomDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY).fieldOf("rogue_loot_table_entries").forGetter(x -> x.rogueLootTableEntries),
			Codec.unboundedMap(Identifier.CODEC, Codec.INT).optionalFieldOf("rarity_overrides", Map.of()).forGetter(x -> x.rarityOverrides),
			Codec.STRING.optionalFieldOf("gui_settings_id", "").forGetter(x -> x.rogueLootGuiSettingsIdString),
	).apply(instance, RogueLootTable::new));

	public RogueLootTable(
			HolderSet<RogueLootTableEntry> rogueLootTableEntries,
			Map<Identifier, Integer> rarityOverrides,
			String rogueLootGuiSettingsIdString,
	) {
		this.rogueLootTableEntries = rogueLootTableEntries;
		this.rarityOverrides = rarityOverrides != null ? new HashMap<>(rarityOverrides) : Map.of();
		this.rogueLootGuiSettingsIdString = rogueLootGuiSettingsIdString != null ? rogueLootGuiSettingsIdString : "";
	}
}
