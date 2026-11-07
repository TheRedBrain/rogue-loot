package com.github.theredbrain.lootableblocks.registry;

import com.github.theredbrain.lootableblocks.data.LootableVaultConfig;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class CustomDynamicRegistries {

	public static final ResourceKey<Registry<LootableVaultConfig>> LOOTABLE_VAULT_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("lootable_vault_configs"));

	public static void init() {
		DynamicRegistries.registerSynced(LOOTABLE_VAULT_CONFIG_REGISTRY_KEY, LootableVaultConfig.CODEC);
	}
}
