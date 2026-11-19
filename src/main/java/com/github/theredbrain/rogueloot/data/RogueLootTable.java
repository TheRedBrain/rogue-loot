package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record RogueLootTable(
		HolderSet<RogueLootTableEntry> rogueLootTableEntries,
		Map<Identifier, Integer> rarityOverrides,
		String rogueLootGuiSettingsIdString,
		boolean enableRerolling,
		Optional<ItemStackTemplate> rerollCost,
		boolean enableEntryBanishing,
		Optional<ItemStackTemplate> banishingCost,
		boolean enableEntryReserving,
		Optional<ItemStackTemplate> reservingCost
) {
	public static final Codec<RogueLootTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.homogeneousList(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY).fieldOf("rogue_loot_table_entries").forGetter(x -> x.rogueLootTableEntries),
			Codec.unboundedMap(Identifier.CODEC, Codec.INT).optionalFieldOf("rarity_overrides", Map.of()).forGetter(x -> x.rarityOverrides),
			Codec.STRING.optionalFieldOf("gui_settings_id", "").forGetter(x -> x.rogueLootGuiSettingsIdString),
			Codec.BOOL.optionalFieldOf("enable_rerolling", false).forGetter(x -> x.enableRerolling),
			ItemStackTemplate.CODEC.optionalFieldOf("reroll_cost").forGetter(x -> x.rerollCost),
			Codec.BOOL.optionalFieldOf("enable_entry_banishing", false).forGetter(x -> x.enableEntryBanishing),
			ItemStackTemplate.CODEC.optionalFieldOf("banishing_cost").forGetter(x -> x.banishingCost),
			Codec.BOOL.optionalFieldOf("enable_entry_reserving", false).forGetter(x -> x.enableEntryReserving),
			ItemStackTemplate.CODEC.optionalFieldOf("reserving_cost").forGetter(x -> x.reservingCost)
	).apply(instance, RogueLootTable::new));

	@Nullable
	public static RogueLootTable getRogueLootTable(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTable rogueLootTable = null;
		Optional<Registry<RogueLootTable>> optionalRogueLootTableRegistry = registryAccess.lookup(RogueLootRegistries.ROGUE_LOOT_TABLE_REGISTRY_KEY);
		if (optionalRogueLootTableRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTable>> optionalRogueLootTableReference = optionalRogueLootTableRegistry.get().get(identifier);
			if (optionalRogueLootTableReference.isPresent()) {
				rogueLootTable = optionalRogueLootTableReference.get().value();
			}
		}
		return rogueLootTable;
	}

}
