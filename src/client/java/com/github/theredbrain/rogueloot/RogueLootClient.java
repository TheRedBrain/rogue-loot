package com.github.theredbrain.rogueloot;

import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.github.theredbrain.rogueloot.renderer.blockentity.LootableVaultRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class RogueLootClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer();
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRenderers.register(EntityRegistry.LOOTABLE_VAULT_BLOCK_ENTITY, LootableVaultRenderer::new);
	}
}