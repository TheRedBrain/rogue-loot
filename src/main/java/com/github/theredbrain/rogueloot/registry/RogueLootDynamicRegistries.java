package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.data.RogueLootVaultConfig;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class RogueLootDynamicRegistries {

	public static final ResourceKey<Registry<RogueLootGuiSettings>> ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_gui_settings"));
	public static final ResourceKey<Registry<RogueLootTable>> ROGUE_LOOT_TABLE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table"));
	public static final ResourceKey<Registry<RogueLootTableEntry>> ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table_entry"));
	public static final ResourceKey<Registry<RogueLootTableEntryRarity>> ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table_entry_rarity"));
	public static final ResourceKey<Registry<RogueLootVaultConfig>> ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_vault_config"));

	public static void init() {
		DynamicRegistries.registerSynced(ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY, RogueLootGuiSettings.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_REGISTRY_KEY, RogueLootTable.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY, RogueLootTableEntry.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY, RogueLootTableEntryRarity.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY, RogueLootVaultConfig.CODEC);
	}
}
