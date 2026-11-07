package com.github.theredbrain.lootableblocks;

import com.github.theredbrain.lootableblocks.registry.BlockRegistry;
import com.github.theredbrain.lootableblocks.registry.EntityRegistry;
import com.github.theredbrain.lootableblocks.render.block.entity.LootableVaultBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderType;

public class LootableBlocksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer();
		registerTransparency();
	}

	private void registerTransparency() {
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.LOOTABLE_VAULT_BLOCK, RenderType.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.LOOTABLE_FROZEN_VAULT_BLOCK, RenderType.getCutout());
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRenderers.register(EntityRegistry.LOOTABLE_VAULT_BLOCK_ENTITY, LootableVaultBlockEntityRenderer::new);
	}
}