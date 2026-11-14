package com.github.theredbrain.rogueloot;

import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacket;
import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacketReceiver;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.github.theredbrain.rogueloot.renderer.blockentity.RogueLootVaultRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class RogueLootClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerBlockEntityRenderer();
		registerPackets();
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRenderers.register(EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY, RogueLootVaultRenderer::new);
	}

	private void registerPackets() {
		PayloadTypeRegistry.clientboundPlay().register(OpenRogueLootScreenPacket.PACKET_ID, OpenRogueLootScreenPacket.PACKET_CODEC);
		ClientPlayNetworking.registerGlobalReceiver(OpenRogueLootScreenPacket.PACKET_ID, new OpenRogueLootScreenPacketReceiver());
	}

}