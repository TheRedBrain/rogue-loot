package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.data.RogueLootVaultConfig;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class RogueLootRegistries {

	public static final ResourceKey<Registry<MapCodec<? extends RogueLootTableEntryComponentType>>> ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table_entry_component_type"));
	public static final ResourceKey<Registry<RogueLootGuiSettings>> ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_gui_settings"));
	public static final ResourceKey<Registry<RogueLootTable>> ROGUE_LOOT_TABLE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table"));
	public static final ResourceKey<Registry<RogueLootTableEntry>> ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table_entry"));
	public static final ResourceKey<Registry<RogueLootTableEntryRarity>> ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_table_entry_rarity"));
	public static final ResourceKey<Registry<RogueLootVaultConfig>> ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("rogue_loot_vault_config"));

	public static Registry<MapCodec<? extends RogueLootTableEntryComponentType>> ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY = FabricRegistryBuilder.create(ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY_KEY).buildAndRegister();

	public static void init() {
		DynamicRegistries.registerSynced(ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY, RogueLootGuiSettings.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_REGISTRY_KEY, RogueLootTable.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY, RogueLootTableEntry.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY, RogueLootTableEntryRarity.CODEC);
		DynamicRegistries.registerSynced(ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY, RogueLootVaultConfig.CODEC);
	}
}
