package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacket;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.serverboundPlay().register(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, UpdateInteractiveRogueLootBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, new UpdateInteractiveRogueLootBlockPacketReceiver());

	}
}
