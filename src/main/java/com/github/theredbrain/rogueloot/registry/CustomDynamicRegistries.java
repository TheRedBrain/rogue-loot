package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.RogueLootVaultConfig;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class CustomDynamicRegistries {

	public static final ResourceKey<Registry<RogueLootVaultConfig>> ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("lootable_vault_configs"));

	public static void init() {
		DynamicRegistries.registerSynced(ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY, RogueLootVaultConfig.CODEC);
	}
}
