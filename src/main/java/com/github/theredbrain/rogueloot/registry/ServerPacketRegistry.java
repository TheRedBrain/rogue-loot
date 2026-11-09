package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveLootBlockPacket;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveLootBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.serverboundPlay().register(UpdateInteractiveLootBlockPacket.PACKET_ID, UpdateInteractiveLootBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateInteractiveLootBlockPacket.PACKET_ID, new UpdateInteractiveLootBlockPacketReceiver());

	}
}
