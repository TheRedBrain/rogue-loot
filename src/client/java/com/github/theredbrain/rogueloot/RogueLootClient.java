package com.github.theredbrain.rogueloot;

import com.github.theredbrain.rogueloot.gui.screen.ingame.RogueLootGUIScreen;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.github.theredbrain.rogueloot.registry.RogueLootMenuTypes;
import com.github.theredbrain.rogueloot.renderer.blockentity.RogueLootVaultRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class RogueLootClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer();
		registerScreens();
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRenderers.register(EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY, RogueLootVaultRenderer::new);
	}

	private void registerScreens() {
		MenuScreens.register(RogueLootMenuTypes.ROGUE_LOOT_GUI_MENU_TYPE, RogueLootGUIScreen::new);
	}

}