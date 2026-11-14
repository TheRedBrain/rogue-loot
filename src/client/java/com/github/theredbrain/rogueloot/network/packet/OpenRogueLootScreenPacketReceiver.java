package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.gui.screen.ingame.RogueLootGUIScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class OpenRogueLootScreenPacketReceiver implements ClientPlayNetworking.PlayPayloadHandler<OpenRogueLootScreenPacket> {

	@Override
	public void receive(final OpenRogueLootScreenPacket payload, final ClientPlayNetworking.Context context) {
		context.client().setScreen(new RogueLootGUIScreen(payload.generatedRogueLoot(), payload.localRogueLootTableIdentifier(), payload.banishingAttempts()));
	}
}
