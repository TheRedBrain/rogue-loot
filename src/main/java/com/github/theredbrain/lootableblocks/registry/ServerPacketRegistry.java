package com.github.theredbrain.lootableblocks.registry;

import com.github.theredbrain.lootableblocks.network.packet.UpdateInteractiveLootBlockPacket;
import com.github.theredbrain.lootableblocks.network.packet.UpdateInteractiveLootBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.serverboundPlay().register(UpdateInteractiveLootBlockPacket.PACKET_ID, UpdateInteractiveLootBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateInteractiveLootBlockPacket.PACKET_ID, new UpdateInteractiveLootBlockPacketReceiver());

	}
}
