package com.github.theredbrain.lootableblocks;

import com.github.theredbrain.lootableblocks.registry.EntityRegistry;
import com.github.theredbrain.lootableblocks.renderer.blockentity.LootableVaultRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class LootableBlocksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer();
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRenderers.register(EntityRegistry.LOOTABLE_VAULT_BLOCK_ENTITY, LootableVaultRenderer::new);
	}
}