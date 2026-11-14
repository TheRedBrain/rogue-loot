package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootDynamicRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record RogueLootTable(
		HolderSet<RogueLootTableEntry> rogueLootTableEntries,
		Map<Identifier, Integer> rarityOverrides,
		String rogueLootGuiSettingsIdString,
		boolean enableEntryBanishing,
		Optional<ItemStackTemplate> banishingCost
) {
	public static final Codec<RogueLootTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.homogeneousList(RogueLootDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY).fieldOf("rogue_loot_table_entries").forGetter(x -> x.rogueLootTableEntries),
			Codec.unboundedMap(Identifier.CODEC, Codec.INT).optionalFieldOf("rarity_overrides", Map.of()).forGetter(x -> x.rarityOverrides),
			Codec.STRING.optionalFieldOf("gui_settings_id", "").forGetter(x -> x.rogueLootGuiSettingsIdString),
			Codec.BOOL.optionalFieldOf("enable_entry_banishing", false).forGetter(x -> x.enableEntryBanishing),
			ItemStackTemplate.CODEC.optionalFieldOf("banishing_cost").forGetter(x -> x.banishingCost)
	).apply(instance, RogueLootTable::new));

	public RogueLootTable(
			final HolderSet<RogueLootTableEntry> rogueLootTableEntries,
			final Map<Identifier, Integer> rarityOverrides,
			final String rogueLootGuiSettingsIdString,
			final boolean enableEntryBanishing,
			final Optional<ItemStackTemplate> banishingCost
	) {
		this.rogueLootTableEntries = rogueLootTableEntries;
		this.rarityOverrides = rarityOverrides != null ? new HashMap<>(rarityOverrides) : Map.of();
		this.rogueLootGuiSettingsIdString = rogueLootGuiSettingsIdString != null ? rogueLootGuiSettingsIdString : "";
		this.enableEntryBanishing = enableEntryBanishing;
		this.banishingCost = banishingCost;
	}
}
