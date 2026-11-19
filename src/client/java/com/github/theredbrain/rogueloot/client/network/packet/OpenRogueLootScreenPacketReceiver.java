package com.github.theredbrain.rogueloot.client.network.packet;

import com.github.theredbrain.rogueloot.client.gui.screen.ingame.RogueLootGUIScreen;
import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class OpenRogueLootScreenPacketReceiver implements ClientPlayNetworking.PlayPayloadHandler<OpenRogueLootScreenPacket> {

	@Override
	public void receive(final OpenRogueLootScreenPacket payload, final ClientPlayNetworking.Context context) {
		context.client().setScreen(new RogueLootGUIScreen(
				payload.generatedRogueLoot(),
				payload.generatedRogueLootTableIdentifier(),
				payload.rerollAttempts(),
				payload.banishingAttempts(),
				payload.reservingAttempts(),
				payload.itemStack()
		));
	}
}
